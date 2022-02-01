package com.legacy.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
	
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
