package com.legacy.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	// [유저 정보]
	@GetMapping("/info")
	public String userInfo() {
		return "user/userInfo";
	}
	
	// [유저 알림]
	@GetMapping("/notify")
	public String userNotify() {
		
		return "user/userNotify";
	}
	
}
