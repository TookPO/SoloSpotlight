package com.legacy.main.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.legacy.user.config.LoginUser;
import com.legacy.user.vo.OAuthAttributes;
import com.legacy.user.vo.SessionUser;

@Controller
@RequestMapping("/")
public class MainController {

	@GetMapping("/")
	public String main(Model model, @LoginUser SessionUser user) { // 성능을 위해 Entity가 아닌 SessionUser을 사용

		model.addAttribute("user", user);
		
		return "main/main";
	}
}
