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
import com.legacy.blog.spot.dao.BlogSpotRepository;
import com.legacy.blog.spot.domain.BlogSpot;
import com.legacy.notify.dao.NotifyRepository;
import com.legacy.notify.domain.Notify;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.User;
import com.legacy.user.vo.UserDto;

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
	private final BlogSpotRepository blogSpotRepository;
	private final NotifyRepository notifyRepository;

	// BLOG_INFO 찾기
	public Map<String, Object> selectHome(Long userId) {
		Map<String, Object> map = new HashMap<>();
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				 .orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		Pageable recommendPaging = PageRequest.of(0, 4);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueImpl(blogInfo.getId(), recommendPaging);
		Pageable postPaging = PageRequest.of(0, 8, Sort.Direction.DESC, "createdDate");
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
	
	@Transactional
	public Map<String, Object> selectPostView(Long postId, Long userId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> blogInfoDto = blogInfoRepository.findByUserIdJoinUser(userId)
				 .orElseThrow(() -> new IllegalArgumentException("블로그가 없습니다."));
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));
		Pageable recommendPaging = PageRequest.of(0, 2);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueNotThisPostIdImpl((Long)blogInfoDto.get("id"), postId, recommendPaging);		
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOne((Long)blogPost.getBlogCategory().getId(),
				blogPost.getId()); // 다른 사람과 자신의 카테고리를 공유하지 않으니까
		if(postCategoryDomainList.isEmpty()) {
			postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOneReverse((Long)blogPost.getBlogCategory().getId(),
					blogPost.getId());
		}
		Pageable spotPaging = PageRequest.of(0, 2);
		List<BlogSpot> blogSpotList = blogSpotRepository.findByFollowerIdRandom(userId, spotPaging); // 블로거의 아이디
		// 조회수 증가
		logger.debug("조회수 전 ->"+blogPost.getViewCount());
		blogPost.updateCount();
		logger.debug("조회수 후 ->"+blogPost.getViewCount());
		
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
		if(!blogSpotList.isEmpty()) {
			List<UserDto> followeeList = new ArrayList<>();
			blogSpotList.forEach((entity) -> {
				followeeList.add(new UserDto(entity.getFolloweeUser()));
			});
			map.put("followeeList", followeeList);
		}
		return map;
	}
	
	public Map<String, Object> selectOnePost(Long postId) {
		Map<String, Object> map = new HashMap<>();
		Optional<BlogPost> blogPost = blogPostRepository.findById(postId);
		if(blogPost.isPresent()) { // 값이 있으면
			map.put("blogPostDto", new BlogPostDto(blogPost.get(), 
					blogPost.get().getBlogCategory().getTitle()));
			map.put("blogInfoDto", new BlogInfoDto(blogPost.get()
					.getBlogInfo()));
			List<BlogCategoryDto> categoryList = new ArrayList<>();
			blogPost.get().getBlogInfo()
			.getBlogCategoryList().forEach((blogCategory) -> {
				categoryList.add(new BlogCategoryDto(blogCategory));
			});
			map.put("categoryList", categoryList);
		}
		
		return map;
	}
	
	@Transactional
	public String updateOnePost(Map<String, Object> data) {
		BlogPost blogPost = blogPostRepository.findById(Long.valueOf(String.valueOf(data.get("postId"))))
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		blogPost.update(data,
				blogCategoryRepository.findById(Long.valueOf(String.valueOf(data.get("categoryId")))).get());
		return String.valueOf(blogPost.getId());
	}
	
	public void deleteOnePost(Long postId) {
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		
		blogPostRepository.delete(blogPost);
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
		if(!data.get("replyAtId").equals("")) {
			notifyRepository.save(Notify.builder()
					.division("replyAt")
					.information(String.valueOf(data.get("replyAtInfo")))
					.message(blogReply.getUser().getName()+"님이 답글을 남겼습니다.")
					.user(userRepository.findById(Long.valueOf(String.valueOf( data.get("replyAtId") ))).get())
					.build());
		}		
		logger.debug("[저장한 REPLY]"+blogReply.getContent());
		if(blogPost == null) {
			return 0L;
		}
		return blogReply.getId();
	}

	public Map<String, Object> selectCategoryListPrev(Long postId) {
		Map<String, Object> map = new HashMap<>();
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOnePrev((Long)blogPost.getBlogCategory().getId(),
				postId);
		if(postCategoryDomainList.isEmpty()) {
			logger.debug("[값이 없음]");
			map.put("ret", "0"); 
		}else {
			logger.debug("[값이 있음]");
			int i =0; // i를 통해서 고유의 key를 만듬
			for(BlogPost entity : postCategoryDomainList){
				map.put("postId"+i, entity.getId());
				map.put("postTitle"+i, entity.getTitle());
				map.put("postDate"+i, entity.getCreatedDate());
				if(entity.getBlogReplyList().isEmpty()) {
					map.put("postMaxReply"+i, "0");
				}else {
					map.put("postMaxReply"+i, entity.getBlogReplyList().size());
				}
				i++;
			}
			map.put("ret", "1");
		}
		logger.debug("[이전 페이지 최종 결과]->"+map.toString());
		return map;
	}

	public Map<String, Object> selectCategoryListNext(Long postId) {
		Map<String, Object> map = new HashMap<>();
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOneNext((Long)blogPost.getBlogCategory().getId(),
				postId);
		if(postCategoryDomainList.isEmpty()) {
			logger.debug("[값이 없음]");
			map.put("ret", "0"); 
		}else {
			logger.debug("[값이 있음]");
			int i =0; // i를 통해서 고유의 key를 만듬
			for(BlogPost entity : postCategoryDomainList){
				map.put("postId"+i, entity.getId());
				map.put("postTitle"+i, entity.getTitle());
				map.put("postDate"+i, entity.getCreatedDate());
				if(entity.getBlogReplyList().isEmpty()) {
					map.put("postMaxReply"+i, "0");
				}else {
					map.put("postMaxReply"+i, entity.getBlogReplyList().size());
				}
				i++;
			}
			map.put("ret", "1");
		}
		logger.debug("[이전 페이지 최종 결과]->"+map.toString());
		return map;
	}

	public String insertSpot(Long followerId, Long followeeId) {
		logger.debug("[아이디]->"+followerId+"/"+followeeId);
		if(followerId.equals(followeeId)) {
			return "same";
		}
		Optional<BlogSpot> blogSpot = blogSpotRepository.findByFolloweeAndFollowerId(followerId, followeeId);
		if(blogSpot.isPresent()) { // 만약 null이 아닐경우
			return "false";
		}
		blogSpotRepository.save(BlogSpot.builder()
				.followerUser(userRepository.findById(followerId).get())
				.followeeUser(userRepository.findById(followeeId).get())
				.build());
		
		return "true";  
	}

	public Map<String, Object> selectSpotList(Long sessionId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Optional<BlogInfoDto> blogInfoDto = blogInfoRepository.findByUserIdDto(sessionId);
		Pageable postPaging = PageRequest.of(0, 9);
		List<Map<String, Object>> blogPostList = blogPostRepository.findByFollowerIdJoinInfoAndPost(sessionId, postPaging); 
		if(blogPostList.isEmpty()) { // null일경우, 전체의 게시글중에 랜덤으로 조회 
			blogPostList = blogPostRepository.findAllRandomNotUserId(sessionId);
		}
		
		// 블로그 정보
		if(blogInfoDto.isPresent()) { // 값이 있을경우
			map.put("blogInfoDto", blogInfoDto.get());
		}else {
			map.put("blogInfoDto", BlogInfoDto.builder()
					.name("내 스폿")
					.headerColor("#FFF9B0")
					.build());
		}
		// 스폿한 사람의 게시물 + 댓글
		logger.debug("게시글 리스트 결과]->"+blogPostList.get(0).get("id"));
		List<Map<String, Object>> postAndReplyList = new ArrayList<>();
		Pageable replyPaging = PageRequest.of(0, 2, Sort.Direction.DESC, "createdDate");
		
		blogPostList.forEach((post) -> {
			Map<String, Object> postAndReply = new HashMap<>();
			// for문으로 빼낸 post
			postAndReply.put("blogPostDto", post); 
			// 좋아요 갯수
			List<Long> blogGoodMax = blogGoodRepository.findByPostIdLong(Long.valueOf(String.valueOf(post.get("id"))));
			postAndReply.put("blogGoodMax", blogGoodMax.size());
			// 댓글 3개
			List<BlogReplyDto> replyList = blogReplyRepository.findByPostId(Long.valueOf(String.valueOf(post.get("id")))
						, replyPaging);
			postAndReply.put("blogReplyDtoList", replyList);
			postAndReplyList.add(postAndReply);
		});
		// map -> postAndReplyList -> postAndReply -> post, goodMax, reply
		map.put("postAndReplyList", postAndReplyList);
		
		return map;
	}
}
