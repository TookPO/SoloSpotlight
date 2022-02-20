package com.legacy.blog.service;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.category.vo.BlogCategoryDto;
import com.legacy.blog.good.dao.BlogGoodRepository;
import com.legacy.blog.good.domain.BlogGood;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.info.vo.BlogInfoDto;
import com.legacy.blog.post.dao.BlogPostRepository;
import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.post.vo.BlogPostDto;
import com.legacy.blog.post.vo.BlogPostDtoImpl;
import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.blog.reply.repository.BlogReplyRepository;
import com.legacy.blog.reply.vo.BlogReplyDto;
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
	private final BlogPostRepository blogPostRepository;
	private final BlogGoodRepository blogGoodRepository;
	private final BlogReplyRepository blogReplyRepository;

	// BLOG_INFO 찾기
	public Map<String, Object> selectHome(Long userId) {
		Map<String, Object> map = new HashMap<>();
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				 .orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		Pageable recommendPaging = PageRequest.of(0, 4);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueImpl(blogInfo.getId(), recommendPaging);
		Pageable postPaging = PageRequest.of(0, 7, Sort.Direction.DESC, "createdDate");
		List<Map<String, Object>> postDtoList = blogPostRepository.findAllRecent(blogInfo.getId(), postPaging);
		
		
		map.put("blogInfoDto", new BlogInfoDto(blogInfo));		
		if(!recommendList.isEmpty()) {
			logger.debug("[리스트 결과]->"+recommendList.get(0).getCreatedDate());
			map.put("recommendList", recommendList);		
		}
		if(!postDtoList.isEmpty()) {
			logger.debug("[최근 게시글 8개]->"+postDtoList.get(0).get("title"));
			map.put("postDtoList", postDtoList);
		}
		return map; 
	}
	
	public Long selectInfoCheck(Long id) {
		BlogInfo blogInfo = blogInfoRepository.findByUserId(id).orElse(null);
		if(blogInfo == null) {
			return 0L;
		}
		return blogInfo.getId();
	}	
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

	public Long insertPost(Long userId, Map<String, Object> data) {
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		BlogCategory blogCategory = blogCategoryRepository.findById(Long.valueOf(String.valueOf(data.get("blogCategoryId"))))
				.orElseThrow(() -> new IllegalArgumentException("카테고리가 없습니다."));
		
		 return blogPostRepository.save(BlogPost.builder()
					.title(String.valueOf(data.get("title")))
					.content(String.valueOf(data.get("content")))
					.thumbnail(String.valueOf(data.get("thumbnail")))
					.viewCount(0L)
					.isPublic(Boolean.parseBoolean(String.valueOf(data.get("isPublic"))))
					.isRecommend(Boolean.parseBoolean(String.valueOf(data.get("isRecommend"))))
					.blogInfo(blogInfo)
					.blogCategory(blogCategory)
					.build()).getId();
	}

	public Map<String, Object> selectPostView(Long postId, Long userId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> blogInfoDto = blogInfoRepository.findByUserIdJoinUser(userId)
				 .orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
		Pageable recommendPaging = PageRequest.of(0, 2);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueNotThisPostIdImpl((Long)blogInfoDto.get("id"), postId, recommendPaging);		
		Pageable postCategoryPaging = PageRequest.of(0, 4, Sort.Direction.DESC, "createdDate");
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOne((Long)blogPost.getBlogCategory().getId() , 
				postCategoryPaging); // 다른 사람과 자신의 카테고리를 공유하지 않으니까
		
		// 블로그 정보 & 블로그 게시물
		map.put("blogInfoDto", blogInfoDto); 
		map.put("blogPostDto", new BlogPostDto(blogPost, 
				  blogPost.getBlogCategory().getTitle()));
		// 추천 리스트
		if(recommendList.isEmpty()) { 
			map.put("recommendList", blogPostRepository.findByIsRecommendFalseNotThisPostIdImpl((Long)blogInfoDto.get("id"), postId, recommendPaging));
		}else {
			map.put("recommendList", recommendList); 
		}
		// 게시글 좋아요
		if(blogPost.getBlogGoodList().isEmpty()) {
			map.put("blogGoodMax", 0);
		}else {
			logger.debug("[받은 리스트 좋아요]->"+blogPost.getBlogGoodList().get(0).getId());
			map.put("blogGoodMax", blogPost.getBlogGoodList().size());			
		}
		// 댓글 리스트
		if(!blogPost.getBlogReplyList().isEmpty()) {
			logger.debug("[받은 리스트 댓글]->"+blogPost.getBlogReplyList().get(0).getContent());
			List<BlogReplyDto> blogReplyDtoList = new ArrayList<>();
			blogPost.getBlogReplyList().forEach((entity) -> {
				blogReplyDtoList.add(new BlogReplyDto(entity, entity.getUser()));
			});
			logger.debug("[담은 REPLY] ->"+blogReplyDtoList.get(0).getWriterName());
			logger.debug("[댓글 사이즈] ->"+blogReplyDtoList.size());
			map.put("blogReplyDtoList", blogReplyDtoList);
		}
		// 해당 카테고리의 게시글
		List<BlogPostDto> postCategoryList = new ArrayList<>();
		postCategoryDomainList.forEach((entity)->{
			postCategoryList.add(new BlogPostDto(entity, entity.getBlogReplyList()));
		});
		map.put("postCategoryList", postCategoryList);
		// 해당 블로거의 스폿
		
		return map;
	}
	
	public Long insertGood(Long postId, Long userId) {
		// 이전 좋아요 확인
		BlogGood blogGood = blogGoodRepository.findByUserIdAndPostId(userId, postId);
		logger.debug("[좋아요 확인]"+blogGood);
		if(blogGood == null) {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
			BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

			return 	blogGoodRepository.save(BlogGood.builder()
					.user(user)
					.blogPost(blogPost)
					.build()).getId();
		}
		return 0L;
	}	
	
	public Long insertReply(Map<String, Object> data, Long writerId) {
		logger.debug("[WRITER ID]->"+writerId);
		User user = userRepository.findById(writerId)
				.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
		BlogPost blogPost = blogPostRepository.findById(Long.valueOf(String.valueOf(data.get("postId"))))
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		BlogReply blogReply = blogReplyRepository.save(BlogReply.builder()
					.content((String)data.get("content"))
					.blogPost(blogPost)
					.user(user)
					.build());
		logger.debug("[저장한 REPLY]"+blogReply.getContent());
		if(blogPost == null) {
			return 0L;
		}
		return blogReply.getId();
	}
}
