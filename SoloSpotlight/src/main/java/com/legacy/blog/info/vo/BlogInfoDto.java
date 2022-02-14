package com.legacy.blog.info.vo;

import com.legacy.blog.info.domain.BlogInfo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BlogInfoDto {
	private String name;
	private String intro;
	private String headerColor;
	private Long revenue;
	
	public BlogInfoDto(BlogInfo blogInfo) {
		this.name = blogInfo.getName();
		this.intro = blogInfo.getIntro();
		this.headerColor = blogInfo.getHeaderColor();
		this.revenue = blogInfo.getRevenue();
	}
}
