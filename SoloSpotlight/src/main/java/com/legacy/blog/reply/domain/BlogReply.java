package com.legacy.blog.reply.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.legacy.blog.post.domain.BlogPost;
import com.legacy.domain.BaseTimeEntity;
import com.legacy.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogReply extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String content;
	
	// blogPost
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_POST_ID")
	private BlogPost blogPost;
	
	// user
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;
	
	@Builder
	public BlogReply(Long id, String content, BlogPost blogPost, User user) {
		this.id = id;
		this.content = content;
		this.blogPost = blogPost;
		this.user = user;
	}
}
