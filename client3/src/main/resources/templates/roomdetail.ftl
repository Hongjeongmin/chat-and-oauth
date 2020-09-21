<!doctype html>
<html lang="en">
  <head>
    <title>웹소켓 테스트용 채팅방입니다.</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
    <style>
      [v-cloak] {
          display: none;
      }
    </style>
  </head>
  <body>
    <div class="container" id="app" v-cloak>
  	<h1>{{myname}} : {{userId}}  {{chatType}}</h1>
        <div class="row">
            <div class="col-md-6">
                <h3>{{chatName}}</h3>
            </div>
    	</div>
    	<button type ="button" @click ="pageLoad()">20개더받기</button>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text">내용</label>
            </div>
            <input type="text" class="form-control" v-model="content" v-on:keypress.enter="sendMessage('TEXT')">
            <div class="input-group-append">
                <button class="btn btn-primary" type="button" @click="sendMessage('TEXT')">보내기</button>
        	</div>    	
   		</div>
        <ul class="list-group">
            <li class="list-group-item" v-for="message in messages">
              id : {{message.id}} userId: {{message.userId}},type :  {{message.type}} :  content: {{message.content}}   sentAt :{{message.sentAt}}  <span class="badge badge-info badge-pill">{{message.unreadCnt}}</span></a>
            </li>
        </ul>
    </div>
    <!-- JavaScript -->
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
    <script>
    	var sock = new SockJS("/ws/chat");
    	var ws = Stomp.over(sock);
    	var reconnect = 0;
    	
        // vue.js
        var vm = new Vue({
            el: '#app',
            data: {
            	myname:'',
            	id: '',
            	name: '',
                chatId: '',
                chatName: '',
                content: '',
                messages: [],
                access_token: '',
                members : '',
                invitedIds: [],
                userId : '',
                chatType:'',
            },
            created() {
            	this.access_token = localStorage.getItem('access_token');
            	this.chatId = localStorage.getItem('wschat.chatId');
            	this.chatName = localStorage.getItem('wschat.chatName');
            	this.myname = localStorage.getItem('myname');
            	this.userId = localStorage.getItem('userId');
            	this.chatType = localStorage.getItem('chatType');
            	
            	var str =localStorage.getItem('wschat.invitedIds');
            	str = str.substring(1,str.length-1);
            	const invitedIds = [];
            	console.log(str);
            	var arr = str.split(",");
            	for(var i =0;i<arr.length;i++){
            		invitedIds.push(arr[i]*1);
            	}
            	this.invitedIds = invitedIds;
            	console.log(this.invitedIds);
            	this.init();
            	// 실행시 초반 20개 불러오기
            	
            },
            methods: {
                sendMessage: function(type) {
                	if(this.chatType=="GROUP"){
	                	if(this.messages.length==0){
	              	      ws.send("/pub/chat/group/join", {}, JSON.stringify({chatId:this.chatId, invitedIds:this.invitedIds}));
	                	}
	                    ws.send("/pub/chat/group/message", {}, JSON.stringify({type:type, chatId:this.chatId, content:this.content}));
	                    this.content = '';
                    }   
                    else if(this.chatType=="PRIVATE"){
                    	ws.send("/pub/chat/private/message", {}, JSON.stringify({type:type, chatId:this.chatId, content:this.content}));
	                    this.content = '';
                    }
                    else if(this.chatType=="SELF"){
                    	ws.send("/pub/chat/self/message", {}, JSON.stringify({type:type, chatId:this.chatId, content:this.content}));
	                    this.content = '';
                    }
                },
                recvMessage: function(recv) {
                    this.messages.unshift({"id":recv.id,"type":recv.type,"userId":recv.userId,"content":recv.content,"sentAt":recv.sentAt,"unreadCnt":recv.unreadCnt})
                },
                recvMessages: function(recv) {
                		console.log(this.messages.length);
                		console.log(recv.length);
                		var len = this.messages.length;
                		var len2 = recv.length;
                		for(i=0;i<len;i++){
                			for(j=0;j<len2;j++){
                					
                				if(recv[j].id == this.messages[i].id){
                					this.messages[i].unreadCnt = recv[j].unreadCnt;
                				}
                			}
                		}
                },
                init: function(){
                	axios.get('/api/chats/'+this.chatId,
                			{
	                   	   headers :{
	                		   Authorization : this.access_token+""
	                	   }
                	})
                	.then(response=>{
                		console.log(response);
                		this.messages = response.data.data.messages.reverse();
                	})
                },
                pageLoad : function(){
                	var len = this.messages.length;
                	axios.get('/api/chats/'+this.chatId+'?lastMessageId='+this.messages[len-1].id,
                			{
	                   	   headers :{
	                		   Authorization : this.access_token+""
	                	   }
                	})
                	.then(response=>{
                		console.log(response);
                		var len = response.data.data.messages.length;
                		for(var i=0;i<len;i++){
                			this.messages.push(response.data.data.messages[len-1-i]);
                		}
                	})
                }
            }
        });
        
    	ws.connect({"Authorization":vm.$data.access_token},function(frame){
    		ws.subscribe("/sub/chat/rooms/"+vm.$data.chatId, function(message) {
                var recv = JSON.parse(message.body);
                console.log(recv.type);
                if(recv.type=="NEWMESSAGE"){
                vm.recvMessage(recv.message);
                }
            });
            ws.subscribe("/sub/chat/unreadCnt/"+vm.$data.chatId+"/"+vm.$data.userId, function(message) {
                var recv = JSON.parse(message.body);
                console.log(recv.type);
               	if(recv.type=="UNREADCNT"){
                vm.recvMessages(recv.messages);
                }
            });
    	}, function(error){
    		alert("서버 연결에 실패 하였습니다. 다시 접속해 주세요");
    		location.href="/";
    	})
    </script>
  </body>
</html>