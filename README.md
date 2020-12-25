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

# Oauth Server

![OuathDB](https://user-images.githubusercontent.com/30316976/103128684-9f561b00-46d9-11eb-98b3-edec8e8efcd9.png)

깃 허브 오픈 소스에서 Data를 참고하였다. 사용자가 수정할 수있게 DB를 수정하였다.

![Type](https://user-images.githubusercontent.com/30316976/103128692-a8df8300-46d9-11eb-95de-701eb91d8522.png)

Google , FaceBook, KaKao등 현재 Oauth를 사용하는 서비스에서 가장 많이 사용하는 방식이다.
3가 인증방식이면 Https에서 정보를 교환한다는 전제에서는 안정성이 매우 뛰어난 인증 플로우이다.

![https://user-images.githubusercontent.com/30316976/103128703-b85ecc00-46d9-11eb-804c-d058681c0e9b.png)

Scope는 토큰의 권한의 유효성이다. 다음과같이 read trust write 세분류로 나눠서 API  요청에대한 권한을 세분화 하여 사용성을 증가시켰다.

# 채팅 서버

![chat DB](https://user-images.githubusercontent.com/30316976/103128706-beed4380-46d9-11eb-9ed0-b259a8325a2e.png)

## 채팅 시스템 설명

![chat Descripion](https://user-images.githubusercontent.com/30316976/103128718-c9a7d880-46d9-11eb-803d-a949de8c6bae.png)








