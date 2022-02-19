package com.legacy.blog.good.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.legacy.blog.post.domain.BlogPost;
import com.legacy.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogGood {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// Blog_INFO
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_POST_ID")
	private BlogPost blogPost;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_USER_ID")
	private User user;
	
	@Builder
	public BlogGood(Long id, BlogPost blogPost, User user) {
		this.id = id;
		this.blogPost = blogPost;
		this.user = user;
	}
}
