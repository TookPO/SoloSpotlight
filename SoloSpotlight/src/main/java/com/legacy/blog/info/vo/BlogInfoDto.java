package com.legacy.blog.info.vo;

import com.legacy.blog.info.domain.BlogInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor // new할때 꼭 있어야함
public class BlogInfoDto {
	private Long id;
	private String name;
	private String intro;
	private String headerColor;
	private Long revenue;
	
	public BlogInfoDto(BlogInfo blogInfo) {
		this.id = blogInfo.getId();
		this.name = blogInfo.getName();
		this.intro = blogInfo.getIntro();
		this.headerColor = blogInfo.getHeaderColor();
		this.revenue = blogInfo.getRevenue();
	}
}
