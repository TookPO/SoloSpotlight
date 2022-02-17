package com.legacy.blog.post.vo;

import java.sql.Date;

public interface BlogPostDtoImpl {
	Long getId();
	String getTitle();
	String getContent();
	String getThumbnail();
	Long getViewCount();
	Integer getGood();
	Boolean getIsPublic();
	Date getCreatedDate();
}
