package com.legacy.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/blog")
public class BlogController {
	
	// [블로그 홈]
	@GetMapping("/{id}")
	public String blogHome(@PathVariable Long id, Model model) {
		
		model.addAttribute("blogName", "지수의 일상 이야기");
		return "blog/blogHome";
	}
	
	// [블로그 생성 입력] 
	@GetMapping("/add")
	public String blogAdd() {
		
		return "blog/blogAdd";
	}
	
	// [블로그 리스트]
	@GetMapping("/{id}/list")
	public String blogList(@PathVariable Long id, Model model) {
		
		model.addAttribute("blogName", "지수의 일상 이야기");
		return "blog/blogList";
	}
	
	// [블로그 글 쓰기]
	@GetMapping("/{id}/add")
	public String blogWrite(@PathVariable Long id, Model model) {
		
		model.addAttribute("blogName", "지수의 일상 이야기");
		return "blog/blogWrite";
	}
	
	// [블로그 글 보기]
	@GetMapping("/{id}/view")
	public String blogView(@PathVariable Long id, Model model) {
		
		model.addAttribute("blogName", "지수의 일상 이야기");
		return "blog/blogView";
	}
	
	// [스폿 리스트]
	@GetMapping("/spot/list")
	public String spotList() {
		
		return "blog/spotList";
	}
	
}
