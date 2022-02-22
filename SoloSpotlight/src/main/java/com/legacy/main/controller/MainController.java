package com.legacy.main.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.legacy.main.service.MainService;
import com.legacy.user.config.LoginUser;
import com.legacy.user.vo.SessionUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {
	Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private final MainService mainService;
	
	@GetMapping("/")
	public String main(Model model) { // 성능을 위해 Entity가 아닌 SessionUser을 사용
		Map<String, Object> map = mainService.selectMain();
		
		return "main/main";
	}
}
