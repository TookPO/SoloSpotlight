package com.legacy.blog.post.domain;

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
import javax.persistence.OneToOne;

import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.good.domain.BlogGood;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.reply.domain.BlogReply;
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
	
	@Column
	private Integer good; // 최종적으로 사용안하면 삭제
	
	@Column
	private Boolean isPublic;
	
	@Column
	private Boolean isDelete;
	
	@Column
	private Boolean isRecommend;
	
	// blogInfo
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_INFO_ID")
	private BlogInfo blogInfo;
	
	// blogCategory
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOG_CATEGORY_ID")
	private BlogCategory blogCategory;
	
	// blogGood
	@OneToMany(mappedBy = "blogPost", fetch = FetchType.LAZY)
	private List<BlogGood> blogGoodList  = new ArrayList<>();
	
	// blogReply
	@OneToMany(mappedBy = "blogPost", fetch = FetchType.LAZY)
	private List<BlogReply> blogReplyList = new ArrayList<>();

	@Builder
	public BlogPost(Long id, String title, String content, String thumbnail, Long viewCount, Integer good,
			Boolean isPublic, Boolean isDelete, Boolean isRecommend, BlogInfo blogInfo, BlogCategory blogCategory,
			List<BlogGood> blogGoodList, List<BlogReply> blogReplyList) {
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
		this.blogGoodList = blogGoodList;
		this.blogReplyList = blogReplyList;
	}
}
