package com.legacy.blog.category.vo;

import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.post.domain.BlogPost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryDto {
	private Long id;
	private String title;
	
	public BlogCategoryDto(BlogCategory blogCategory) {
		this.id = blogCategory.getId();
		this.title = blogCategory.getTitle();
	}
}
