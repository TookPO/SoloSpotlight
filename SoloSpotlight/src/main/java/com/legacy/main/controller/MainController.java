package com.legacy.main.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.legacy.mail.service.MailService;
import com.legacy.main.service.MainService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {
	Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private final MainService mainService;
	private final MailService mailService;
	
	@GetMapping("/")
	public String main(Model model) { // 성능을 위해 Entity가 아닌 SessionUser을 사용
		Map<String, Object> map = mainService.selectMain();
		model.addAttribute("popPostList", map.get("popPostList"));		
		model.addAttribute("popUserList", map.get("popUserList"));
		
		return "main/main";
	}
	
	@PostMapping("/inquiry")
	@ResponseBody
	public void inquiryMail(@RequestBody Map<String, Object> data) {
		mailService.sendInquiryMail(data);	
	}
}
