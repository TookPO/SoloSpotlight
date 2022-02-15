package com.legacy.blog.category.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.domain.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogCategory extends BaseTimeEntity {
	
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

	public void update(String title) {
		this.title = title;
	}
	
}
