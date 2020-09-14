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
        <div class="row">
            <div class="col-md-6">
                <h3>{{chatName}}</h3>
            </div>
    	</div>
        <div class="input-group">
            <div class="input-group-prepend">
                <label class="input-group-text">내용</label>
            </div>
            <input type="text" class="form-control" v-model="content" v-on:keypress.enter="sendMessage('text')">
            <div class="input-group-append">
                <button class="btn btn-primary" type="button" @click="sendMessage('text')">보내기</button>
        	</div>    	
   		</div>
        <ul class="list-group">
            <li class="list-group-item" v-for="message in messages">
               id: {{message.userId}},type :  {{message.type}} :  content: {{message.content}}   sentAt :{{message.sentAt}}</a>
            </li>
        </ul>
    </div>
    <!-- JavaScript -->
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
    <script>
    	var sock = new SockJS("/ws-chat");
    	var ws = Stomp.over(sock);
    	var reconnect = 0;
    	
        // vue.js
        var vm = new Vue({
            el: '#app',
            data: {
            	id: '',
            	name: '',
                chatId: '',
                chatName: '',
                content: '',
                messages: [],
                access_token: '',
            },
            created() {
            	this.access_token = localStorage.getItem('access_token');
            	this.chatId = localStorage.getItem('wschat.chatId');
            	this.chatName = localStorage.getItem('wschat.chatName');
            	this.init();
            	// 실행시 초반 20개 불러오기
            	
            },
            methods: {
                sendMessage: function(type) {
                    ws.send("/pub/chat/message", {"Authorization":this.access_token}, JSON.stringify({type:type, chatId:this.chatId, content:this.content}));
                    this.content = '';
                },
                recvMessage: function(recv) {
                    this.messages.unshift({"type":recv.type,"userId":recv.userId,"content":recv.content,"sentAt":recv.sentAt})
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
                		this.messages = response.data.data.messages;
                	})
                }
            }
        });
    	ws.connect({},function(frame){
    		ws.subscribe("/sub/chat/rooms/"+vm.$data.chatId, function(message) {
                var recv = JSON.parse(message.body);
                vm.recvMessage(recv);
            });
    	}, function(error){
    		alert("서버 연결에 실패 하였습니다. 다시 접속해 주세요");
    		location.href="/";
    	})
    </script>
  </body>
</html>