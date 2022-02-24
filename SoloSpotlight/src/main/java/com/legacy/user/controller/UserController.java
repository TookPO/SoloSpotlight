package com.legacy.user.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String userInfo(@RequestParam("id") Long userId, Model model) {
		Map<String, Object> map = userService.selectInfo(userId);
		model.addAttribute("userDto", map.get("userDto"));
		model.addAttribute("replyDtoList", map.get("replyDtoList"));
		return "user/userInfo";
	}
	
	// [유저 알림]
	@GetMapping("/notify")
	public String userNotify(@RequestParam(value="filter", required = true, defaultValue="all")String filter, 
			@LoginUser SessionUser user, Model model) {
		if(user == null) {
			return "nullLogin";
		}
		List<Map<String, Object>> notifyList = userService.selectNotifyList(user.getId(), filter);
		model.addAttribute("notifyList", notifyList);
		
		return "user/userNotify";
	}
	
}
