package com.legacy.blog.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.legacy.blog.service.BlogService;
import com.legacy.user.config.LoginUser;
import com.legacy.user.vo.SessionUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {
	Logger logger = LoggerFactory.getLogger(BlogController.class);
	
	private final BlogService blogService;
	
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
	
	// [블로그 생성 완료]
	@PostMapping("/add")
	@ResponseBody
	public void blogAddDone(@RequestBody Map<String, Object> data,
			@LoginUser SessionUser user) {
		logger.debug("[ADD에서 받은 값]"+data);
		String[] array = data.get("category")
				.toString().replaceAll("\\]|\\[| ", "")
				.split(",");
		logger.debug("[Array] ->"+Arrays.toString(array));
		List<String> categoryList = Arrays.asList(array);
		// 등록하기
		blogService.insertInfo(data, user.getId(), categoryList);
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
	
	// [블로그 관리]
	@GetMapping("/admin")
	public String blogAdmin(Model model) {
		
		model.addAttribute("blogName", "지수의 일상 이야기");
		return "blog/blogAdmin";
	}
	
	// [스폿 리스트]
	@GetMapping("/spot/list")
	public String spotList() {
		
		return "blog/spotList";
	}
	
}
