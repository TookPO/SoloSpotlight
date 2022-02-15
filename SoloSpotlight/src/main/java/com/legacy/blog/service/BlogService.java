package com.legacy.blog.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.category.vo.BlogCategoryDto;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.info.vo.BlogInfoDto;
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
		logger.debug("[blogInfo]->"+blogInfo.getName());
		List<BlogCategory> saveList = new ArrayList<>();
		categoryList.forEach(category -> saveList.add(new BlogCategory().builder()
				.title(category)
				.blogInfo(blogInfo)
				.build()));
		
		blogCategoryRepository.saveAll(saveList); // 카테고리 등록
	}
	
	// BLOG_INFO 찾기
	public BlogInfoDto selectHome(Long userId) {	
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				 .orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		
		return new BlogInfoDto(blogInfo);
	}

	public Map<String, Object> selectInfoUpdate(Long userId) {
		Map<String, Object> map = new HashMap<>();
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		List<String> categoryList = blogCategoryRepository.findByBlogInfoIdString(blogInfo.getId());		
		map.put("blogInfoDto", new BlogInfoDto(blogInfo));
		if(categoryList.isEmpty()) { 
			return map; 
		} 
		map.put("categoryList", categoryList);
		
		return map;
	}

	public void updateInfo(Map<String, Object> data, Long id, List<String> categoryList) {
		// 블로그 등록
		BlogInfo blogInfo = blogInfoRepository.findByUserId(id)
				.orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		
		blogInfo.update((String)data.get("name"), 
				(String)data.get("intro"), (String)data.get("headerColor"));
		
		// 카테고리 등록
		List<BlogCategory> entityList = blogCategoryRepository.findByBlogInfoId(blogInfo.getId());
		logger.debug("[카테고리 리스트] ->"+entityList.get(0).getTitle());
		logger.debug("[카테고리 리스트] ->"+entityList.get(1).getTitle());		
		logger.debug("[카테고리 사이즈] ->"+entityList.size()+"/ 수정 할 사이즈 ->"+categoryList.size());
		int i = 0;
		for(String category: categoryList) {
			if(categoryList.size() < entityList.size()){ // 카테고리 수를 줄이려는 경우
				// 되는 이유: 1번이라도 무조건 거쳐야함
				logger.debug("[카테고리 삭제]");
				blogCategoryRepository.deleteById(entityList.get(entityList.size() - 1).getId());
				entityList.remove(entityList.size() - 1);
			}
		
			if((entityList.size()-1) < i) { // 카테고리 수를 늘리려는 경우
				logger.debug(i+"[카테고리 if문]->"+(entityList.size()-1));
				logger.debug("[새로운 카테고리]->"+category);
				blogCategoryRepository.save(BlogCategory.builder()
						.title(category)
						.blogInfo(blogInfo)
						.build());
			}else{
				entityList.get((i++)).update(category);
				logger.debug("[업데이트 된 카테고리] ->"+category);
			}
		}
	}

	public Map<String, Object> selectPostAdd(Long userId) {
		Map<String, Object> map = new HashMap<>();
		// dto
		BlogInfoDto blogInfoDto = blogInfoRepository.findByUserIdDto(userId)
				.orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		// category
		List<BlogCategoryDto> categoryList = blogCategoryRepository.findByBlogInfoIdDto(blogInfoDto.getId());
		logger.debug("[결과 카테고리] ->"+categoryList.toString());
		map.put("blogInfoDto", blogInfoDto);
		map.put("categoryList", categoryList);
		
		return map;
	}
}
