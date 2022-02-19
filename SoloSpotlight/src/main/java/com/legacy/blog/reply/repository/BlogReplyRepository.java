package com.legacy.blog.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.legacy.blog.reply.domain.BlogReply;

public interface BlogReplyRepository extends JpaRepository<BlogReply, Long> {

}
