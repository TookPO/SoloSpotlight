package com.legacy.blog.category.vo;

import com.legacy.blog.category.domain.BlogCategory;

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
