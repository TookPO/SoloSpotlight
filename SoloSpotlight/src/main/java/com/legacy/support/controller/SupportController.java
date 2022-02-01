package com.legacy.support.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support")
public class SupportController {
	
	// [자주 묻는 질문(메인)]
	@GetMapping("/home")
	public String supportHome() {
		
		return "support/supportHome";
	}
	
	@GetMapping("/add")
	public String supportAdd() {
		
		return "support/supportAdd";
	}
	
}
