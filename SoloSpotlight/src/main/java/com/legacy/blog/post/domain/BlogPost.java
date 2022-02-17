package com.legacy.blog.post.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.domain.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogPost extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String content;
	
	@Column
	private String thumbnail;
	
	@Column(nullable = false)
	private Long viewCount;
	
	@Column(nullable = false)
	private Integer good;
	
	@Column
	private Boolean isPublic;
	
	@Column
	private Boolean isDelete;
	
	@Column
	private Boolean isRecommend;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_INFO_ID")
	private BlogInfo blogInfo;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_CATEGORY_ID")
	private BlogCategory blogCategory;

	@Builder	
	public BlogPost(Long id, String title, String content, String thumbnail, Long viewCount, Integer good,
			Boolean isPublic, Boolean isDelete, Boolean isRecommend, BlogInfo blogInfo, BlogCategory blogCategory) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.viewCount = viewCount;
		this.good = good;
		this.isPublic = isPublic;
		this.isDelete = isDelete;
		this.isRecommend = isRecommend;
		this.blogInfo = blogInfo;
		this.blogCategory = blogCategory;
	}
}
