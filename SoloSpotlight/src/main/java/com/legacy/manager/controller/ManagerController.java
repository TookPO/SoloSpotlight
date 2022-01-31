package com.legacy.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	// [관리자 비지니스 화면]
	@GetMapping("/home")
	public String managerHome() {

		return "manager/managerHome";
	}
}
