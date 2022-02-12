package com.legacy.blog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogService {
	private static Logger logger = LoggerFactory.getLogger(BlogService.class);
	
	private final BlogInfoRepository blogInfoRepository;
	private final UserRepository userRepository;
	private final BlogCategoryRepository blogCategoryRepository;
	
	// BLOG_INFO 추가
	@Transactional
	public void insertInfo(Map<String, Object> data, Long id, List<String> categoryList) {
		// 연관관계 등록을 위해 User Entity를 find
		Optional<User> user = userRepository.findById(id);
		logger.debug("[찾은 user ->"+user.get().getEmail());
		BlogInfo blogInfo = BlogInfo.builder()
				.name((String) data.get("name"))
				.intro((String)data.get("intro"))
				.headerColor((String)data.get("headerColor"))
				.revenue(0L)
				.user(user.get())
				.build();
		
		blogInfoRepository.save(blogInfo); // 블로그 등록
		List<BlogCategory> saveList = new ArrayList<>();
		categoryList.forEach(category -> saveList.add(new BlogCategory().builder()
				.title(category)
				.blogInfo(blogInfo)
				.build()));
		
		blogCategoryRepository.saveAll(saveList); // 카테고리 등록
	}

}
