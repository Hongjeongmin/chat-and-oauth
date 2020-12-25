# chat-and-oauth
```
 Spring Security를 사용하여서 Oauth 서버의 구축과 채팅서버를 구축하였습니다.
```
# Teach Stack
![Teach Stack](https://user-images.githubusercontent.com/30316976/103128511-01fae700-46d9-11eb-9968-eaf04db930fc.png)
* Spring Boot
* Mybaits
* MySql
* Spring Security
* Oauth 2.0
* Redis
* Maven
* Stomp

# 전체 시스템 설명
![PreView](https://user-images.githubusercontent.com/30316976/103128673-95ccb300-46d9-11eb-8b11-46a0a0f10055.png)

ChatServer에서 로그인을 할 때 Oauth(인증/인가)서버에서 Jwt Token을 발급 받아서 이를 활용하여 Api를 요청하고 User에대한 정보를 받는다.

# Oauth Server DB Schema
