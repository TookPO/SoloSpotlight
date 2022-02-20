package com.legacy.blog.category.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.post.domain.BlogPost;
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
	
	// [blogInfo] 참조
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_INFO_ID")
	private BlogInfo blogInfo;
	
	// [blogPost]
	@OneToMany(mappedBy="blogCategory", fetch = FetchType.LAZY)
	private List<BlogPost> blogPostList = new ArrayList<>();	
	
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
