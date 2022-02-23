package com.legacy.user.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.legacy.blog.good.domain.BlogGood;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.blog.spot.domain.BlogSpot;
import com.legacy.notify.domain.Notify;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(exclude = {"followeeList", "followerList"})
@Entity
public class User{	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String email;
	
	@Column
	private String picture;
	
	@Enumerated(EnumType.STRING) // 기본은 int
	@Column(nullable = false)
	private Role role;
	
	@Column
	private int age;
	
	@Column
	private String location;
	
	@Column
	private String job;
	
	@Column
	private int prohibit;
	
	// [notify]
	@OneToMany(mappedBy = "user", cascade = {CascadeType.REMOVE}) // 소유자... / 삭제 시킬때 연관관계 모두 삭제 시켜라...
	private List<Notify> notifyList = new ArrayList<Notify>();
	
	// [blogInfo]
	@OneToOne(mappedBy="user", fetch = FetchType.LAZY)
	private BlogInfo blogInfo;
	
	// [blogGood]
	@OneToMany(mappedBy="user", fetch = FetchType.LAZY)
	private List<BlogGood> blogGoodList = new ArrayList<>();
	
	// [blogReply]
	@OneToMany(mappedBy="user", fetch = FetchType.LAZY)
	private List<BlogReply> blogReplyList = new ArrayList<>();
	
	// [blogSpot] follower
	@OneToMany(mappedBy="followerUser", fetch = FetchType.LAZY)
	private List<BlogSpot> followerList = new ArrayList<>();
	
	// [blogSpot] followee
	@OneToMany(mappedBy="followeeUser", fetch = FetchType.LAZY)
	private List<BlogSpot> followeeList = new ArrayList<>();
	
	@Builder
	public User(Long id, String name, String email, String picture, Role role, int age, String location, String job,
			int prohibit, List<Notify> notifyList, BlogInfo blogInfo, List<BlogGood> blogGoodList,
			List<BlogReply> blogReplyList) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.role = role;
		this.age = age;
		this.location = location;
		this.job = job;
		this.prohibit = prohibit;
		this.notifyList = notifyList;
		this.blogInfo = blogInfo;
		this.blogGoodList = blogGoodList;
		this.blogReplyList = blogReplyList;
	}
	
	public User update(String name, String picture) {
		this.name = name;
		this.picture = picture;
		
		return this;
	}
	
	public String getRoleKey() {
		return this.role.getKey();
	}
}
