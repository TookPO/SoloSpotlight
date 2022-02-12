package com.legacy.blog.category.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.legacy.blog.info.domain.BlogInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="BLOG_CATEGORY")
@Getter
@NoArgsConstructor
public class BlogCategory {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private BlogInfo blogInfo;
	
	@Builder
	public BlogCategory(Long id, String title, BlogInfo blogInfo) {
		this.id = id;
		this.title = title;
		this.blogInfo = blogInfo;
	}
	
}
