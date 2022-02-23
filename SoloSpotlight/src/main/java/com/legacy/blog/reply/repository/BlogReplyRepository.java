package com.legacy.blog.reply.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.blog.reply.vo.BlogReplyDto;

public interface BlogReplyRepository extends JpaRepository<BlogReply, Long> {
	
	@Query("SELECT "
			+ "new com.legacy.blog.reply.vo.BlogReplyDto(r.id AS id, r.content AS content, r.createdDate AS createdDate, u.id AS writerId, u.name AS writerName, u.picture AS writerPicture) "
			+ "FROM BlogReply r INNER JOIN r.blogPost p INNER JOIN r.user u "
			+ "WHERE p.id = :postId ")
	List<BlogReplyDto> findByPostId(@Param("postId")Long postId, Pageable replyPaging);
	
	@Query("SELECT "
			+ "r "
			+ "FROM BlogReply r INNER JOIN r.user u "
			+ "WHERE u.id = :userId ")
	List<BlogReply> findByUserId(@Param("userId")Long userId, Pageable page);
}
