package com.legacy.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.legacy.user.config.LoginUser;
import com.legacy.user.service.UserService;
import com.legacy.user.vo.SessionUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	
	private final UserService userService;
	
	// [유저 정보]
	@GetMapping("/info")
	public String userInfo() {
		return "user/userInfo";
	}
	
	// [유저 알림]
	@GetMapping("/notify")
	public String userNotify(@LoginUser SessionUser user) {
		
		return "user/userNotify";
	}
	
}
