package com.legacy.main.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.legacy.blog.post.dao.BlogPostRepository;
import com.legacy.blog.post.vo.BlogPostDto;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.vo.UserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainService {
	Logger logger = LoggerFactory.getLogger(MainService.class);
	
	private final BlogPostRepository blogPostRepository;
	private final UserRepository userRepository;
	
	public Map<String, Object> selectMain() {
		Map<String, Object> map = new HashMap<>();
		// 인기있는 게시글 5개
		Pageable popPostPaging = PageRequest.of(0, 6, Sort.Direction.DESC, "viewCount");
		List<Map<String, Object>> popPostList = blogPostRepository.findByPopPost(popPostPaging);
		// 인기있는 블로거 6명
		Pageable popUserPaging = PageRequest.of(0, 5);
		List<Map<String, Object>> popUserList = userRepository.findByPopUser(popUserPaging);
		
		map.put("popPostList", popPostList);
		map.put("popUserList", popUserList);
		
		return map;
	}
	
	
}
