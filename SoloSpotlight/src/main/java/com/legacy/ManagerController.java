package com.legacy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	
	// [관리자 비지니스 화면]
	@GetMapping("/home")
	public String managerHome() {
		System.out.println("hello batis");
		return "manager/managerHome";
	}
}
