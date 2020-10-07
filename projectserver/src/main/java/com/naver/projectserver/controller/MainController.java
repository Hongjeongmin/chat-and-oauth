package com.naver.projectserver.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.naver.projectserver.common.RandomValue;
import com.naver.projectserver.mapper.Client_manager;
import com.naver.projectserver.mapper.Oauth_client_details;
import com.naver.projectserver.mapper.User;
import com.naver.projectserver.service.Client_managerService;
import com.naver.projectserver.service.Oauth_client_detailsService;
import com.naver.projectserver.service.UserService;
/*
 * 인증서버 화면 관련 Controller
 */

@Controller
public class MainController {
	@Autowired
	UserService userService;

	@Autowired
	RandomValue randomValue;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	Oauth_client_detailsService ocdService;

	@Autowired
	Client_managerService client_managerService;
	
	//root 화
	@GetMapping("/")
	public ModelAndView main(Model model, Principal principal) {
		/*
		 * 초기 로그인시 principal의 값에 따라서 null 값이면 home으로 null이 아니면 clinet 등록가능한 화면으로 이동한다.
		 */
		ModelAndView m = null;

		if (principal == null) {
			m = new ModelAndView("home");
			m.addObject("message", "인증 되지 않은 사람입니다. 인증에 제한이됩니다.");
		} else {
			m = new ModelAndView("main");
			m.addObject("message", principal.getName() + "님 안녕하세요 인증이 된사람입니다.");
		}
		return m;
	}
	// login 화면
	@GetMapping("login")
	public String loginForm() {
		return "login";
	}
	// logout 화면
	@GetMapping("logout")
	public String logoutForm() {
		return "logout";
	}
	// signup 화면
	@GetMapping("/signup")
	public String signupForm() {
		return "signup";
	}
	// signup 처리 성공시 login 화면
	@PostMapping("signup")
	public String signupProccess(HttpServletRequest request) {
		User user = new User();
		user.setUsername(request.getParameter("username"));
		user.setPwd(request.getParameter("pwd"));
		user.setName(request.getParameter("name"));
		user.setPhone(request.getParameter("phone"));
		user.setEmail(request.getParameter("email"));
		userService.signup(user);
		return "login";
	}
	// 비밀번호찾기 화면
	@GetMapping("forgot")
	public String forgotForm() {
		return "forgot";
	}
	// 비밀번호찾기 처리
	@PostMapping("forgot")
	public ModelAndView forgotProccess(HttpServletRequest request) {

		String username = request.getParameter("username");
		ModelAndView m = new ModelAndView("checkpass");
		/*
		 * 비밀번호는 임의의 6자리 숫자로 출력한다.
		 */
		if (userService.login(username) != null) {
			String newPass = randomValue.getRandomcode(6);
			User user = new User();
			user.setUsername(username);
			user.setPwd(newPass);
			if (userService.update(user)) {
				m.addObject("message", "비밀 번호 찾기 성공 : " + newPass);
			} else {
				m.addObject("message", "비밀번호 변경과정에서 실패 했습니다.");
			}
		} else {
			m.addObject("message", "비밀 번호 찾기 실패 아이디를 확인해 주세요");
		}
		return m;
	}
	
	// Client App 등록된 리스트 화면
	@GetMapping("oauthlist")
	public ModelAndView oauthlist(Principal principal) {
		ModelAndView m = new ModelAndView("oauthlist");
		List<Client_manager> clinet_managers = client_managerService.select(principal.getName());
		m.addObject("client_managers", clinet_managers);

		return m;
	}
	
	// Client App 등록 화면
	@GetMapping("getoauth")
	public String getoauthForm() {

		return "getoauth";
	}
	
	//  Client App 등록 처리
	@Transactional
	@PostMapping("getoauth")
	public String getoauthProccess(@RequestParam List<String> authorized_grant_types, @RequestParam List<String> scopes,
			@RequestParam("web_server_redirect_uri") String web_server_redirect_uri,
			@RequestParam("appname") String appname, Principal principal) {
		/*
		 * input values scope,grant_type은 ','로 구분한다.
		 */

		Client_manager client_manager = new Client_manager();
		client_manager.Create_client_manger(principal.getName(), appname, randomValue, web_server_redirect_uri);

		Oauth_client_details ocd = new Oauth_client_details();
		ocd.setClient_id(client_manager.getClient_id());
		ocd.setWeb_server_redirect_uri(web_server_redirect_uri);
		ocd.setClient_secret(client_manager.getClient_secret());
		ocd.setAuthorized_grant_types(String.join(",", authorized_grant_types));
		ocd.setScope(String.join(",", scopes));

		client_managerService.insert(client_manager);
		ocdService.insert(ocd);

		return "redirect:/";
	}
	
	// Client App 삭제 처리
	@Transactional
	@GetMapping("delete")
	public ModelAndView delete(@RequestParam("client_id") String client_id, Principal principal) {
		ModelAndView m = new ModelAndView("oauthlist");
		List<Client_manager> clinet_managers = client_managerService.select(principal.getName());
		m.addObject("client_managers", clinet_managers);

		client_managerService.delete(client_id);
		ocdService.delete(client_id);
		return m;
	}
	
	// Client App update 처리
	@Transactional
	@GetMapping("update")
	public ModelAndView update(@RequestParam("client_id") String client_id, Principal principal) {
		Client_manager cm = new Client_manager();
		Oauth_client_details ocd = new Oauth_client_details();

		cm.setUsername(principal.getName());
		cm.setClient_id(client_id);
		cm.setClient_secret(randomValue.getRandomcode(60));
		ocd.setClient_id(client_id);
		ocd.setClient_secret(cm.getClient_secret());
		client_managerService.updateSecret(cm);
		ocdService.update(ocd);
		ModelAndView m = new ModelAndView("oauthlist");
		List<Client_manager> clinet_managers = client_managerService.select(principal.getName());
		m.addObject("client_managers", clinet_managers);
		
		return m;
	}

}
