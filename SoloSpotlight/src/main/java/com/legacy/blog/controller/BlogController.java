package com.legacy.blog.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.legacy.blog.reply.vo.BlogReplyDto;
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
	@GetMapping("/{userId}")
	public String blogHome(@PathVariable Long userId, Model model) {
		logger.debug("[{userId}]->"+userId);
		try {
			Map<String, Object> map = blogService.selectHome(userId);
			logger.debug("[결과] ->"+map.toString());
			model.addAttribute("userId", userId);
			model.addAttribute("blogInfoDto", map.get("blogInfoDto"));
			model.addAttribute("recommendList", map.get("recommendList"));
			model.addAttribute("postDtoList", map.get("postDtoList"));
		}catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("userId", userId);
			return "blog/blogNull";
		}
	
		return "blog/blogHome";
	}
	
	// [블로그 생성 입력] 
	@GetMapping("/add")
	public String blogAdd(@LoginUser SessionUser user) {
		if(user == null) {
			return "blog/blogNull";
		}
		Long userId = blogService.selectInfoCheck(user.getId());
		if(userId > 0L) {
			return "blog/blogCheck";
		}
		
		return "blog/blogAdd";
	}
	
	// [블로그 생성 완료]
	@PostMapping("/add")
	@ResponseBody
	public String blogAddDone(@RequestBody Map<String, Object> data,
			@LoginUser SessionUser user) {
		logger.debug("[ADD에서 받은 값]"+data);
		String[] array = data.get("category")
				.toString().replaceAll("\\]|\\[| ", "")
				.split(",");
		logger.debug("[Array] ->"+Arrays.toString(array));
		
		List<String> categoryList = Arrays.asList(array);
		// 등록하기
		blogService.insertInfo(data, user.getId(), categoryList);
		
		return Long.toString(user.getId());
	}
	
	// [블로그 업데이트]
	@GetMapping("/update")
	public String blogUpdate(@LoginUser SessionUser user, Model model) {
		Map<String, Object> map = blogService.selectInfoUpdate(user.getId());
		model.addAttribute("userId", user.getId());
		model.addAttribute("blogInfoDto", map.get("blogInfoDto"));
		
		return "blog/blogUpdate";
	}
	
	// [블로그 업데이트 완료]
	@PutMapping("/update")
	@ResponseBody
	private String blogUpdateDone(@RequestBody Map<String, Object> data,
			@LoginUser SessionUser user) {
		logger.debug("[UPDATE에서 받은 값]"+data);
		String[] array = data.get("category")
				.toString().replaceAll("\\]|\\[| ", "")
				.split(",");
		logger.debug("[Array] ->"+Arrays.toString(array));
		
		List<String> categoryList = Arrays.asList(array);
		// 등록 후 성공 결과로 아이디를 받아옴
		blogService.updateInfo(data, user.getId(), categoryList);
		
		return Long.toString(user.getId());
	}
	
	// [블로그 리스트] * 보류 *
	@GetMapping("/{userId}/list")
	public String blogList(@PathVariable Long userId, Model model) { return "blog/blogList"; }
	
	// [블로그 글쓰기]
	@GetMapping("/{userId}/add")
	public String blogWrite(@PathVariable Long userId, Model model) {
		logger.debug("[블로그 글쓰기]");
		Map<String, Object> map = blogService.selectPostAdd(userId);		
		model.addAttribute("userId", userId);
		model.addAttribute("blogInfoDto", map.get("blogInfoDto"));
		model.addAttribute("categoryList", map.get("categoryList"));
		return "blog/blogWrite";
	}
	
	// [글쓰기 완료]
	@PostMapping("/{userId}/add")
	@ResponseBody
	public String blogWriteDone(@PathVariable("userId")Long userId, 
			@RequestBody Map<String, Object> data) {
		logger.debug("[게시글 작성]->"+data.toString());
		Long blogPostId = blogService.insertPost(userId, data);
		logger.debug("[받은 아이디]->"+blogPostId);
		return Long.toString(blogPostId);
	}
	
	// [블로그 글보기]
	@GetMapping("/{userId}/view")
	public String blogView(@RequestParam("id") Long postId, 
			@PathVariable Long userId, Model model) {
		logger.debug("[블로그 글 조회]");
		Map<String, Object> map = blogService.selectPostView(postId, userId);
		logger.debug("[결과] ->"+map.toString());
		model.addAttribute("userId", userId);
		model.addAttribute("blogInfoDto", map.get("blogInfoDto"));
		model.addAttribute("blogPostDto", map.get("blogPostDto"));
		model.addAttribute("recommendList", map.get("recommendList"));
		model.addAttribute("blogGoodMax", map.get("blogGoodMax"));
		model.addAttribute("blogReplyDtoList", map.get("blogReplyDtoList"));
		model.addAttribute("postCategoryList", map.get("postCategoryList"));
		return "blog/blogView";
	}
	
	// [블로그 좋아요 등록]
	@PostMapping("/{userId}/good/add")
	@ResponseBody
	public String goodAdd(@RequestBody Map<String, Object> data,
			@LoginUser SessionUser user) {
		Long goodId = blogService.insertGood(Long.valueOf(String.valueOf(data.get("postId"))), 
				user.getId());
		return Long.toString(goodId);
	}
	
	// [블로그 댓글 등록]
	@PostMapping("{userId}/reply/add")
	@ResponseBody
	public String blogReplyAdd(@RequestBody Map<String, Object> data, @LoginUser SessionUser user) {	
		logger.debug("[댓글 쓰기] ->"+data.toString()+"/ userWriter->"+user.getId());	
		return Long.toString(blogService.insertReply(data, user.getId()));
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
