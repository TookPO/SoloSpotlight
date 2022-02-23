package com.legacy.user.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.blog.reply.repository.BlogReplyRepository;
import com.legacy.notify.dao.NotifyRepository;
import com.legacy.user.dao.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	Logger logger = LoggerFactory.getLogger(UserService.class);
	
	private final NotifyRepository notifyRepository;
	private final UserRepository userRepository;
	private final BlogReplyRepository blogReplyRepository;
	
	public List<Map<String, Object>> recentThree(Long id) {
		Pageable paging = PageRequest.of(0, 2, Sort.Direction.DESC, "id");
		return notifyRepository.findByUserId(id, paging);
	}
	
	@Transactional
	public Map<String, Object> selectInfo(Long userId) {
		Map<String, Object> map = new HashMap<>();
		// 유저 정보 + 전체 스폿 
		Map<String, Object> userDto = userRepository.findByUserAndSpotSize(userId);
		logger.debug("[받은 spot 총 개수]->"+userDto.get("followeeSize"));
		// 유저의 최근 활동 10개만
		Pageable page = PageRequest.of(0, 10, Sort.Direction.DESC, "createdDate");
		List<BlogReply> blogReplyList = blogReplyRepository.findByUserId(userId, page);
		logger.debug("[받은 reply]->"+blogReplyList.get(0).getContent());
		List<Map<String, Object>> replyDtoList = new ArrayList<>();
		blogReplyList.forEach((blogReply) -> {
			Map<String, Object> replyDto = new HashMap<>();
			// 해당 댓글의 게시글 정보를 받아옴
			// 게시글로 갈수 있게 링크: userId, postId
			replyDto.put("userId", blogReply.getBlogPost()
					.getBlogInfo().getUser().getId());
			replyDto.put("postId", blogReply.getBlogPost()
					.getId());
			// 제목: p.title
			replyDto.put("title", blogReply.getBlogPost()
					.getTitle());
			// 썸네일: p.thumbnail
			replyDto.put("thumbnail", blogReply.getBlogPost()
					.getThumbnail());
			// 작성자
			replyDto.put("writerName", blogReply.getBlogPost()
					.getBlogInfo().getUser().getName());
			replyDtoList.add(replyDto);
			logger.debug("replyDto 담김]->"+replyDto.get("title"));
		});
		map.put("userDto", userDto);
		map.put("replyDtoList", replyDtoList);
		return map;
	}
	
	
}
