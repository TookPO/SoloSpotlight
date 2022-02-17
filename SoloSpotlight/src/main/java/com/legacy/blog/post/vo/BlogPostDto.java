package com.legacy.blog.post.vo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.legacy.blog.post.domain.BlogPost;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlogPostDto {
	private Long id;
	private String title;
	private String content;
	private String thumbnail;
	private Long viewCount;
	private Integer good;
	private Boolean isPublic;
	private LocalDateTime createdDate;
	private String categoryTitle;
	
	// repository에서 받기 위해
	public BlogPostDto(Long id, String title, String content, String thumbnail, Long viewCount, Integer good,
			Boolean isPublic, LocalDateTime createdDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.viewCount = viewCount;
		this.good = good;
		this.isPublic = isPublic;
		this.createdDate = createdDate;
	}
	
	@Builder
	public BlogPostDto(BlogPost blogPost, String categoryTitle) {
		this.id = blogPost.getId();
		this.title = blogPost.getTitle();
		this.content = blogPost.getContent();
		this.thumbnail = blogPost.getThumbnail();
		this.viewCount = blogPost.getViewCount();
		this.good = blogPost.getGood();
		this.isPublic = blogPost.getIsPublic();
		this.categoryTitle = categoryTitle;
	}	
}
