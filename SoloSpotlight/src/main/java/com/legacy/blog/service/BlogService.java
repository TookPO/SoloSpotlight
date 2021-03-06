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

	// BLOG_INFO ??????
	public Map<String, Object> selectHome(Long userId) {
		Map<String, Object> map = new HashMap<>();
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				 .orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		Pageable recommendPaging = PageRequest.of(0, 4);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueImpl(blogInfo.getId(), recommendPaging);
		Pageable postPaging = PageRequest.of(0, 8, Sort.Direction.DESC, "createdDate");
		List<Map<String, Object>> postDtoList = blogPostRepository.findAllRecent(blogInfo.getId(), postPaging);
		
		map.put("blogInfoDto", new BlogInfoDto(blogInfo));		
		if(!recommendList.isEmpty()) {
			logger.debug("[????????? ??????]->"+recommendList.get(0).getCreatedDate());
			map.put("recommendList", recommendList);		
		}
		if(!postDtoList.isEmpty()) {
			logger.debug("[?????? ????????? 8???]->"+postDtoList.get(0).get("title"));
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
	// BLOG_INFO ??????
	@Transactional
	public void insertInfo(Map<String, Object> data, Long id, List<String> categoryList) {
		// ???????????? ????????? ?????? User Entity??? find
		Optional<User> user = userRepository.findById(id);
		logger.debug("[?????? user ->"+user.get().getEmail());
		BlogInfo blogInfo = BlogInfo.builder()
				.name((String) data.get("name"))
				.intro((String)data.get("intro"))
				.headerColor((String)data.get("headerColor"))
				.revenue(0L)
				.user(user.get())
				.build();
		
		blogInfoRepository.save(blogInfo); // ????????? ??????
		logger.debug("[blogInfo]->"+blogInfo.getName());
		List<BlogCategory> saveList = new ArrayList<>();
		categoryList.forEach(category -> saveList.add(new BlogCategory().builder()
				.title(category)
				.blogInfo(blogInfo)
				.build()));
		
		blogCategoryRepository.saveAll(saveList); // ???????????? ??????
	}
	
	public Map<String, Object> selectInfoUpdate(Long userId) {
		Map<String, Object> map = new HashMap<>();
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		List<String> categoryList = blogCategoryRepository.findByBlogInfoIdString(blogInfo.getId());		
		map.put("blogInfoDto", new BlogInfoDto(blogInfo));
		if(categoryList.isEmpty()) { 
			return map; 
		} 
		map.put("categoryList", categoryList);
		
		return map;
	}

	public void updateInfo(Map<String, Object> data, Long id, List<String> categoryList) {
		// ????????? ??????
		BlogInfo blogInfo = blogInfoRepository.findByUserId(id)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		
		blogInfo.update((String)data.get("name"), 
				(String)data.get("intro"), (String)data.get("headerColor"));
		
		// ???????????? ??????
		List<BlogCategory> entityList = blogCategoryRepository.findByBlogInfoId(blogInfo.getId());
		logger.debug("[???????????? ?????????] ->"+entityList.get(0).getTitle());	
		logger.debug("[???????????? ?????????] ->"+entityList.size()+"/ ?????? ??? ????????? ->"+categoryList.size());
		int i = 0;
		for(String category: categoryList) {
			if(categoryList.size() < entityList.size()){ // ???????????? ?????? ???????????? ??????
				// ?????? ??????: 1???????????? ????????? ????????????
				logger.debug("[???????????? ??????]");
				blogCategoryRepository.deleteById(entityList.get(entityList.size() - 1).getId());
				entityList.remove(entityList.size() - 1);
			}
		
			if((entityList.size()-1) < i) { // ???????????? ?????? ???????????? ??????
				logger.debug(i+"[???????????? if???]->"+(entityList.size()-1));
				logger.debug("[????????? ????????????]->"+category);
				blogCategoryRepository.save(BlogCategory.builder()
						.title(category)
						.blogInfo(blogInfo)
						.build());
			}else{
				entityList.get((i++)).update(category);
				logger.debug("[???????????? ??? ????????????] ->"+category);
			}
		}
	}

	public Map<String, Object> selectPostAdd(Long userId) {
		Map<String, Object> map = new HashMap<>();
		// dto
		BlogInfoDto blogInfoDto = blogInfoRepository.findByUserIdDto(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		// category
		List<BlogCategoryDto> categoryList = blogCategoryRepository.findByBlogInfoIdDto(blogInfoDto.getId());
		logger.debug("[?????? ????????????] ->"+categoryList.toString());
		map.put("blogInfoDto", blogInfoDto);
		map.put("categoryList", categoryList);
		
		return map;
	}

	public Long insertPost(Long userId, Map<String, Object> data) {
		BlogInfo blogInfo = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		BlogCategory blogCategory = blogCategoryRepository.findById(Long.valueOf(String.valueOf(data.get("blogCategoryId"))))
				.orElseThrow(() -> new IllegalArgumentException("??????????????? ????????????."));
		
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
				 .orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		Pageable recommendPaging = PageRequest.of(0, 2);
		List<BlogPostDtoImpl> recommendList = blogPostRepository.findByIsRecommendTrueNotThisPostIdImpl((Long)blogInfoDto.get("id"), postId, recommendPaging);		
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOne((Long)blogPost.getBlogCategory().getId(),
				blogPost.getId()); // ?????? ????????? ????????? ??????????????? ???????????? ????????????
		if(postCategoryDomainList.isEmpty()) {
			postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOneReverse((Long)blogPost.getBlogCategory().getId(),
					blogPost.getId());
		}
		Pageable spotPaging = PageRequest.of(0, 2);
		List<BlogSpot> blogSpotList = blogSpotRepository.findByFollowerIdRandom(userId, spotPaging); // ???????????? ?????????
		// ????????? ??????
		logger.debug("????????? ??? ->"+blogPost.getViewCount());
		blogPost.updateCount();
		logger.debug("????????? ??? ->"+blogPost.getViewCount());
		
		// ????????? ?????? & ????????? ?????????
		map.put("blogInfoDto", blogInfoDto); 
		map.put("blogPostDto", new BlogPostDto(blogPost, 
				  blogPost.getBlogCategory().getTitle()));
		// ?????? ?????????
		if(recommendList.isEmpty()) { 
			map.put("recommendList", blogPostRepository.findByIsRecommendFalseNotThisPostIdImpl((Long)blogInfoDto.get("id"), postId, recommendPaging));
		}else {
			map.put("recommendList", recommendList); 
		}
		// ????????? ?????????
		if(blogPost.getBlogGoodList().isEmpty()) {
			map.put("blogGoodMax", 0);
		}else {
			logger.debug("[?????? ????????? ?????????]->"+blogPost.getBlogGoodList().get(0).getId());
			map.put("blogGoodMax", blogPost.getBlogGoodList().size());			
		}
		// ?????? ?????????
		if(!blogPost.getBlogReplyList().isEmpty()) {
			logger.debug("[?????? ????????? ??????]->"+blogPost.getBlogReplyList().get(0).getContent());
			List<BlogReplyDto> blogReplyDtoList = new ArrayList<>();
			blogPost.getBlogReplyList().forEach((entity) -> {
				blogReplyDtoList.add(new BlogReplyDto(entity, entity.getUser()));
			});
			logger.debug("[?????? REPLY] ->"+blogReplyDtoList.get(0).getWriterName());
			logger.debug("[?????? ?????????] ->"+blogReplyDtoList.size());
			map.put("blogReplyDtoList", blogReplyDtoList);
		}
		// ?????? ??????????????? ?????????
		List<BlogPostDto> postCategoryList = new ArrayList<>();
		postCategoryDomainList.forEach((entity)->{
			postCategoryList.add(new BlogPostDto(entity, entity.getBlogReplyList()));
		});
		map.put("postCategoryList", postCategoryList);
		// ?????? ???????????? ??????
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
		if(blogPost.isPresent()) { // ?????? ?????????
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
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		blogPost.update(data,
				blogCategoryRepository.findById(Long.valueOf(String.valueOf(data.get("categoryId")))).get());
		return String.valueOf(blogPost.getId());
	}
	
	public void deleteOnePost(Long postId) {
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		
		blogPostRepository.delete(blogPost);
	}	
	
	public Long insertGood(Long postId, Long userId) {
		// ?????? ????????? ??????
		BlogGood blogGood = blogGoodRepository.findByUserIdAndPostId(userId, postId);
		logger.debug("[????????? ??????]"+blogGood);
		if(blogGood == null) {
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
			BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));

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
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		BlogPost blogPost = blogPostRepository.findById(Long.valueOf(String.valueOf(data.get("postId"))))
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		BlogReply blogReply = blogReplyRepository.save(BlogReply.builder()
					.content((String)data.get("content"))
					.blogPost(blogPost)
					.user(user)
					.build());
		if(!data.get("replyAtId").equals("")) {
			notifyRepository.save(Notify.builder()
					.division("replyAt")
					.information(String.valueOf(data.get("replyAtInfo")))
					.message(blogReply.getUser().getName()+"?????? ????????? ???????????????.")
					.user(userRepository.findById(Long.valueOf(String.valueOf( data.get("replyAtId") ))).get())
					.build());
		}		
		logger.debug("[????????? REPLY]"+blogReply.getContent());
		if(blogPost == null) {
			return 0L;
		}
		return blogReply.getId();
	}

	public Map<String, Object> selectCategoryListPrev(Long postId) {
		Map<String, Object> map = new HashMap<>();
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOnePrev((Long)blogPost.getBlogCategory().getId(),
				postId);
		if(postCategoryDomainList.isEmpty()) {
			logger.debug("[?????? ??????]");
			map.put("ret", "0"); 
		}else {
			logger.debug("[?????? ??????]");
			int i =0; // i??? ????????? ????????? key??? ??????
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
		logger.debug("[?????? ????????? ?????? ??????]->"+map.toString());
		return map;
	}

	public Map<String, Object> selectCategoryListNext(Long postId) {
		Map<String, Object> map = new HashMap<>();
		BlogPost blogPost = blogPostRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		List<BlogPost> postCategoryDomainList = blogPostRepository.findByInfoIdAndCategoryOneNext((Long)blogPost.getBlogCategory().getId(),
				postId);
		if(postCategoryDomainList.isEmpty()) {
			logger.debug("[?????? ??????]");
			map.put("ret", "0"); 
		}else {
			logger.debug("[?????? ??????]");
			int i =0; // i??? ????????? ????????? key??? ??????
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
		logger.debug("[?????? ????????? ?????? ??????]->"+map.toString());
		return map;
	}

	public String insertSpot(Long followerId, Long followeeId) {
		logger.debug("[?????????]->"+followerId+"/"+followeeId);
		if(followerId.equals(followeeId)) {
			return "same";
		}
		Optional<BlogSpot> blogSpot = blogSpotRepository.findByFolloweeAndFollowerId(followerId, followeeId);
		if(blogSpot.isPresent()) { // ?????? null??? ????????????
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
		if(blogPostList.isEmpty()) { // null?????????, ????????? ??????????????? ???????????? ?????? 
			blogPostList = blogPostRepository.findAllRandomNotUserId(sessionId);
		}
		
		// ????????? ??????
		if(blogInfoDto.isPresent()) { // ?????? ????????????
			map.put("blogInfoDto", blogInfoDto.get());
		}else {
			map.put("blogInfoDto", BlogInfoDto.builder()
					.name("??? ??????")
					.headerColor("#FFF9B0")
					.build());
		}
		// ????????? ????????? ????????? + ??????
		logger.debug("????????? ????????? ??????]->"+blogPostList.get(0).get("id"));
		List<Map<String, Object>> postAndReplyList = new ArrayList<>();
		Pageable replyPaging = PageRequest.of(0, 2, Sort.Direction.DESC, "createdDate");
		
		blogPostList.forEach((post) -> {
			Map<String, Object> postAndReply = new HashMap<>();
			// for????????? ?????? post
			postAndReply.put("blogPostDto", post); 
			// ????????? ??????
			List<Long> blogGoodMax = blogGoodRepository.findByPostIdLong(Long.valueOf(String.valueOf(post.get("id"))));
			postAndReply.put("blogGoodMax", blogGoodMax.size());
			// ?????? 3???
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
