package com.naver.client.controller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naver.client.common.BasicImage;
import com.naver.client.common.JwtTokenProvider;
import com.naver.client.dto.OauthInfo;
import com.naver.client.mapper.ChatUser;
import com.naver.client.oauth.DaEun;
import com.naver.client.oauth.JeongMin;
import com.naver.client.oauth.NaDan;
import com.naver.client.oauth.WooJae;
import com.naver.client.repo.ImageRepo;
import com.naver.client.service.ChatUserService;

@Controller
public class MainController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	ChatUserService chatUserService;
	
	@Autowired
	DaEun daEun;
	
	@Autowired
	JeongMin jeongMin;
	
	@Autowired
	NaDan naDan;
	
	@Autowired
	WooJae wooJae;
	
	@Autowired
	ImageRepo imageRepo;
	
	@Autowired
	ModelMapper modelMapper;
	private final Resource indexPage;
	public MainController(@Value("classpath:/static/index.html") Resource indexPage) {
		this.indexPage = indexPage;
	}

	@GetMapping(value = {"/", "/mail","/LoginPage"})
	public ResponseEntity<Resource> getFirstPage() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<>(indexPage, headers, HttpStatus.OK);
	}

	@GetMapping("/oauth2/authorization/{server}")
	public String redirectOauth(@PathVariable("server") String server) {
		OauthInfo oauthInfo = new OauthInfo();
		if("jeongmin".equals(server)) {
			oauthInfo = modelMapper.map(jeongMin, OauthInfo.class);
		}else if("woojae".equals(server)) {
			oauthInfo = modelMapper.map(wooJae, OauthInfo.class);
		}else if("nathan".equals(server)) {
			oauthInfo = modelMapper.map(naDan, OauthInfo.class);
		}else if("daeun".equals(server)) {
			oauthInfo = modelMapper.map(daEun, OauthInfo.class);
		}
		
		return  "redirect:"+oauthInfo.target_uri+"/oauth/authorize?client_id="+oauthInfo.client_id+"&redirect_uri="+oauthInfo.redirect_uri+"&response_type=code&scope="+oauthInfo.scope;
		
	}
	
	
	@GetMapping("/oauth/callback/{server}")
	public String Callback(@RequestParam("code") String code, HttpServletResponse response,
			@PathVariable("server") String server) throws JsonProcessingException, IOException {
		ResponseEntity<String> responseEntity = null;
		RestTemplate restTemplate = new RestTemplate();
		OauthInfo oauthInfo = new OauthInfo();
		
		/*
		 * 인증 서버별 호출함수 셋팅
		 */
		if("WooJae".equals(server)) {
			oauthInfo = modelMapper.map(wooJae, OauthInfo.class);
		}else if("NaDan".equals(server)) {
			oauthInfo = modelMapper.map(naDan, OauthInfo.class);
			
		}else if("JeongMin".equals(server)) {
			oauthInfo = modelMapper.map(jeongMin, OauthInfo.class);
			
		}else if("DaEun".equals(server)) {
			oauthInfo = modelMapper.map(daEun, OauthInfo.class);
			
		}
		
		String credentials = oauthInfo.client_id + ":" + oauthInfo.secret;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);

		String access_token_url = oauthInfo.getTarget_uri() + "/oauth/token";
		access_token_url += "?code=" + code;
		access_token_url += "&grant_type=authorization_code";
		access_token_url += "&redirect_uri=" + oauthInfo.redirect_uri;

		responseEntity = restTemplate.exchange(access_token_url, HttpMethod.POST, request, String.class);

		/*
		 *  Get the Access Token From the recieved JSON response
		 */
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(responseEntity.getBody());
		String access_token = node.path("access_token").asText();
		String refresh_token = node.path("refresh_token").asText();

		String url = oauthInfo.getTarget_uri() + oauthInfo.getApi();

		/*
		 *  Use the access token for authentication
		 */
		headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + access_token);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		node = mapper.readTree(responseEntity.getBody());
		String id = server + "_" + node.path("id").asText();
		String name = node.path("name").asText();
		String email = node.path("email").asText();
		String phone = node.path("phone").asText();

		ChatUser chatUser = new ChatUser();
		chatUser.setName(name);
		chatUser.setUsername(id);
		chatUser.setRefresh_token(refresh_token);
		chatUser.setAccess_token(access_token);
		chatUser.setImage(imageRepo.selectOne(BasicImage.getRandomInt()));
		
		/*
		 * server + _username으로 username을 만든다. 이때 이미 존재하는 user 라면 기존의 id값을 반환한다.
		 * 이미 있는 정보에 대해서는 DUPLICATE KEY UPDATE 중복 방지한다.
		 */
		chatUserService.insert(chatUser);
		/*
		 * PK유저 아이디를 불러온다.
		 */
		int userId = chatUserService.selectIdAsUsername(chatUser.getUsername());
		
		/*
		 * userId에 RandomImage를 저장한다.
		 */
		/*
		 * token 으로 변화
		 */
		String token = jwtTokenProvider.generateToken(String.valueOf(userId));
		/*
		 * 쿠키 저장
		 */
		Cookie cookie = new Cookie("access-token", token);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24);
		cookie.setHttpOnly(false);
		response.addCookie(cookie);
		
		return "/";
	}
	
	//TODO 추후 제거 예정 현재는 테스트용으로 쓰는 중....
	@GetMapping("/chat/room/enter")
	public String tokenTest(@Header("token") String token) {
		return "roomdetail";
	}
}
