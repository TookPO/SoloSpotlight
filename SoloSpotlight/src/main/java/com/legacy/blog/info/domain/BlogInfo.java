package com.legacy.blog.info.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.post.domain.BlogPost;
import com.legacy.domain.BaseTimeEntity;
import com.legacy.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogInfo extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) // h2 오류로 인해서 잠시만 바꿔둠
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String intro;
	
	@Column(nullable = false)
	private String headerColor;
	
	@Column(nullable = false)
	private Long revenue;
	
	// [user] 참조
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;
	
	// [info]
	@OneToMany(mappedBy="blogInfo", fetch = FetchType.LAZY)
	List<BlogCategory> blogCategoryList = new ArrayList<>();
	
	// [blogPost]
	@OneToMany(mappedBy="blogInfo", fetch = FetchType.LAZY)
	private List<BlogPost> blogPostList;
	
	@Builder
	public BlogInfo(Long id, String name, String intro, String headerColor, Long revenue, User user,
			List<BlogCategory> blogCategoryList, List<BlogPost> blogPostList) {
		this.id = id;
		this.name = name;
		this.intro = intro;
		this.headerColor = headerColor;
		this.revenue = revenue;
		this.user = user;
		this.blogCategoryList = blogCategoryList;
		this.blogPostList = blogPostList;
	}
	
	public void update(String name, String intro, String headerColor) {
		this.name = name;
		this.intro = intro;
		this.headerColor = headerColor;
	}
}
