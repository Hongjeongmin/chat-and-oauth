<!doctype html>
<html lang="en">
<head>
<title>Websocket Chat</title>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<!-- CSS -->
<link rel="stylesheet"
	href="/webjars/bootstrap/4.3.1/dist/css/bootstrap.min.css">
<style>
[v-cloak] {
	display: none;
}
</style>
</head>
<body>
	<div class="container" id="app" v-cloak>
		<h1>{{myname}}</h1>
	
		<div v-if="access_token !='' && access_token!='undefined'">
			<div class="row">
				<div class="col-md-6">
					<h3>전체 유저</h3>
					<p v-for="user in users" v-bind:key="user.id">
						id : {{user.id}} , name : {{user.name}} <br>
					</p>
				</div>			
			
				<br>
				<div class="col-md-6">
					<h3>채팅방 리스트</h3>
				</div>
				<div class="col-md-6 text-right">
					<button type="button" @click="logout()">로그아웃</button>
				</div>
			</div>
			<div class="input-group">
				<div class="input-group-prepend">
					<label class="input-group-text">방제목</label>
				</div>
				<input type="text" class="form-control" v-model="room_name">
				<div class="input-group-prepend">
					<label class="input-group-text">친구 초대 ("," 로 구분)</label>
				</div>
				<input type="text" class="form-control" v-model="friends">				
				<div class="input-group-append">
					<button class="btn btn-primary" type="button" @click="createRoom">채팅방 개설</button>
				</div>
			</div>
			
	        <ul class="list-group">
	            <li class="list-group-item list-group-item-action" v-for="chat in chatrooms" v-bind:key="chat.id">
	                <div v-on:click="enterRoom(chat.id, chat.name,chat.members,chat.type)"> <h6>id :{{chat.id}} 방제목 : {{chat.name}} , 타입 : {{chat.type}}, lastAt : {{chat.lastAt}}, lastMessage : {{chat.lastMessage}}<span class="badge badge-info badge-pill">{{chat.unreadCnt}}</span></h6></div>
	           		 <button @click="deleteRoom(chat.id)">나가기</button>
	            </li>
	        </ul>			

		</div>
		
		<div v-if="access_token ===''">
			<a href ="/oauth2/authorization/jeongmin">정민 로그인</a><br>
			<a href ="/oauth2/authorization/woojae">우재 로그인</a><br>
			<a href ="/oauth2/authorization/nathan">나단 로그인</a><br>
			<a href ="/oauth2/authorization/daeun">다은 로그인</a><br>
		
		
 			<!-- <a href="http://10.106.93.88:8090/oauth/authorize?client_id=207c813d-3b10-4e06-b752-32f3da69d87f&redirect_uri=http://localhost:8080/oauth/callback/WooJae&response_type=code&scope=name email phone">우재 로그인 </a> <br> 
			<a href="http://10.113.93.169:8080/oauth/authorize?client_id=3756e414-22b0-414d-8f19-f02e3b3b12c5&redirect_uri=http://localhost:8080/oauth/callback/NaDan&response_type=code&scope=name email phone">나단 로그인 </a> <br> 
			<a href="http://10.113.98.87:8080/oauth/authorize?client_id=M6vYDUHzrpy32G06qe3c8YY7Ehb8Eh1A9W1Qg6Tn4efvn4A442lF37o1TX9b&redirect_uri=http://localhost:8080/oauth/callback/JeongMin&response_type=code&scope=read write trust">정민 로그인 </a> <br> 
			<a href="">다은 로그인 </a> <br> -->
			
			<!-- <a href="http://10.106.93.88:8090/oauth/authorize?client_id=cf374e83-54ae-495e-8d37-42ce22addf7e&redirect_uri=http://10.113.100.202:8080/oauth/callback/WooJae&response_type=code&scope=name email phone">우재 로그인 </a> <br> 
			<a href="http://10.113.93.169:8080/oauth/authorize?client_id=1aa5cac8-768b-4053-aba6-a07124746643&redirect_uri=http://10.113.100.202:8080/oauth/callback/NaDan&response_type=code&scope=name email phone">나단 로그인 </a> <br> 
			<a href="http://10.113.98.87:8080/oauth/authorize?client_id=16Qy2329K8P1YVTj5w516021NplecWQP27JK2JqgLR3xtqIP0KTP272Oe492&redirect_uri=http://10.113.100.202:8080/oauth/callback/JeongMin&response_type=code&scope=read write trust">정민 로그인 </a> <br> 
			<a href="">다은 로그인 </a> <br> -->
		</div>
	</div>
	<!-- JavaScript -->
	<script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
	<script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
	<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>

	<script>
	    var sock = new SockJS("/ws/chat");
    	var ws = Stomp.over(sock);
        var vm = new Vue({
            el: '#app',
            data: {
            	myname :'',
               	access_token : '',
                room_name : '',
                friendIds :[],
                chatrooms: [],
                users:[],
                friends : "",
                friendsSplit :[],
                tmp:[],
                name:'',
                userId:'',
            },
            created() {
            	if($.cookie('access_token')!=null){
            		console.log('test');
            		this.access_token = $.cookie('access_token');
            		this.invalid();
            		this.findAllRoom();
            		this.findAllUsers();
            		var _this = this;
            		ws.connect({"Authorization":_this.access_token},function(frame){
			    		ws.subscribe("/sub/chat/list/"+_this.userId, function(message) {
			                var recv = JSON.parse(message.body);
			                vm.recvMessage(recv);
			            });
			
			    	}, function(error){
			    	})
            	}
            	
            },
            methods: {
                logout: function() {
                	$.removeCookie('access_token');
                	this.access_token='';
                },
               	deleteRoom: function(id) {
               		var chatIds =[];
               		chatIds.push(id*1);
               		axios.post('api/chats/delete',{
               			chatIds : chatIds,
               		}
               		
               		,{
               			headers:{
               				Authorization : this.access_token
               			}
               		}).then(data=>{
               		})
                },
                invalid: function(){	
               		axios.get('/api/check',{
               			headers:{
               				Authorization : this.access_token
               			}
               		}).then(data=>{
               			this.myname = data.data.data.user.name;
               			this.userId = data.data.data.user.id;
               		})
                },
                createRoom: function() {
                   // if("" === this.room_name) {
                   //     alert("방 제목을 입력해 주십시요.");
                   //     return;
                   // } else {
                    	
                      	this.friendsSplit = this.friends.split(',');
                      	this.friendIds=[];
                    	for(var index=0;index<this.friendsSplit.length;index++){
                    		if(this.friendsSplit[index]!=' ')
                    			this.friendIds.push(parseInt(this.friendsSplit[index]))
                    	}
                    	console.log(this.friendIds);
                       axios.post('/api/chats',{
                    	   "image": "https://ww.namu.la/s/c090d6ba217e99e4906b9748e915a3d51c3712d79b05e10ce0eccae25f5f342385bf1621cbe538fda0f69ec9aaeede028ab7ab42c97b01f82dce7c666e31fe059a35fdad29101713302c100fc42395b90d6b0c2322515f7bc2d190e50632c0db",
                    	   "name": this.room_name,
                    	   "friendIds": this.friendIds
                       },{
                    	   headers :{
                    		   Authorization : this.access_token
                    	   }
                       }
                       
                       ).then(response=>{
                    	   this.room_name = '';
                    	   alert("방 개설에 성공하였습니다.")
                    	   this.findAllRoom();
                       }).catch(response =>{alert("채팅발 개설 실패")});
             	  //  }
           		},
           		findAllRoom : function(){
           			axios.get('/api/chats',{
           				headers :{
           					Authorization : this.access_token
           				}
           			}).then(data=>{
           				this.chatrooms = data.data.data.chats;
           			});
           		},
           		findAllUsers : function(){
           			axios.get('/api/users',{
           				headers :{
           					Authorization : this.access_token
           				}
           			}).then(data=>{
           				this.users = data.data.data.users;
           			});
           		},
           		enterRoom: function(id,name,members,type){
           			localStorage.setItem('wschat.chatId',id);
           			localStorage.setItem('wschat.chatName',name);
           			localStorage.setItem('access_token',this.access_token);
           			localStorage.setItem('myname',this.myname);
           			localStorage.setItem('userId',this.userId);
           			localStorage.setItem('chatType',type);
           			var invitedIds =[];
           			for(var i=0;i<members.length;i++){
           				invitedIds.push(members[i].id);
           			}
           			var str = invitedIds.join(',')
           			localStorage.setItem('wschat.invitedIds',JSON.stringify(str));
           			location.href="/chat/room/enter";
           		},
           		recvMessage: function(recv) {
           			if("EXIST" == recv.type){
           				var len = this.chatrooms.length;
           				for(var i=0;i<len;i++){
           					if(recv.chat.id == this.chatrooms[i].id){
           						this.chatrooms[i].lastAt = recv.chat.lastAt;
           						this.chatrooms[i].lastMessage = recv.chat.lastMessage;
           						this.chatrooms[i].unreadCnt = recv.chat.unreadCnt;
           					}
           				}
           			}
           			if("NEW" == recv.type){
           				this.chatrooms.unshift({"id" : recv.chat.id, "name" : recv.chat.name, "lastMessage" : recv.chat.lastMessage, "lastAt": recv.chat.lastAt, "type" : recv.chat.type, "unreadCnt" : recv.chat.unreadCnt, "members":recv.chat.members});
           			}
                }
       		 }
            
        });
    </script>
</body>
</html>