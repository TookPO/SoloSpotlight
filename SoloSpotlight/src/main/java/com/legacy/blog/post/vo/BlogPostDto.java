package com.legacy.blog.post.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.reply.domain.BlogReply;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
	private Boolean isDelete;
	private Boolean isRecommend;	
	private LocalDateTime createdDate;
	private String categoryTitle;
	private int replyMax;
	
	public BlogPostDto(BlogPost blogPost, String categoryTitle) {
		this.id = blogPost.getId();
		this.title = blogPost.getTitle();
		this.content = blogPost.getContent();
		this.thumbnail = blogPost.getThumbnail();
		this.viewCount = blogPost.getViewCount();
		this.isPublic = blogPost.getIsPublic();
		this.isDelete = blogPost.getIsDelete();
		this.isRecommend = blogPost.getIsRecommend();
		this.createdDate = blogPost.getCreatedDate();	
		this.categoryTitle = categoryTitle;
	}
	
	public BlogPostDto(BlogPost blogPost, List<BlogReply> replyList) {
		this.id = blogPost.getId();
		this.title = blogPost.getTitle();
		this.content = blogPost.getContent();
		this.thumbnail = blogPost.getThumbnail();
		this.viewCount = blogPost.getViewCount();
		this.isPublic = blogPost.getIsPublic();
		this.createdDate = blogPost.getCreatedDate();
		if(replyList.isEmpty()) {
			this.replyMax = 0;
		}else {
			this.replyMax = replyList.size();
		}		
	}	

	public BlogPostDto(Long id, String title, String content, String thumbnail, Long viewCount, 
			Boolean isPublic, LocalDateTime createdDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.thumbnail = thumbnail;
		this.viewCount = viewCount;
		this.isPublic = isPublic;
		this.createdDate = createdDate;
	}

	public BlogPostDto(BlogPost blogPost) {
		this.id = blogPost.getId();
		this.title = blogPost.getTitle();
		this.content = blogPost.getContent();
		this.thumbnail = blogPost.getThumbnail();
		this.viewCount = blogPost.getViewCount();
		this.isPublic = blogPost.getIsPublic();
		this.isDelete = blogPost.getIsDelete();
		this.isRecommend = blogPost.getIsRecommend();
		this.createdDate = blogPost.getCreatedDate();	
	}	
}
