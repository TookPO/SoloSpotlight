package com.legacy.blog.reply.vo;

import java.time.LocalDateTime;

import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class BlogReplyDto {
	private Long id;
	private String content;
	private LocalDateTime createdDate;
	private Long writerId;
	private String writerName;
	private String writerPicture;
	
	public BlogReplyDto(BlogReply blogReply, User user) {
		this.id = blogReply.getId();
		this.content = blogReply.getContent();
		this.createdDate = blogReply.getCreatedDate();
		this.writerId = user.getId();
		this.writerName = user.getName();
		this.writerPicture = user.getPicture();
	}
}
