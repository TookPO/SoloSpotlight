package com.legacy.blog.spot.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.legacy.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BlogSpot {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOLLOWER_USER_ID")
	private User followerUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FOLLOWEE_USER_ID")
	private User followeeUser;

	@Builder
	public BlogSpot(Long id, User followerUser, User followeeUser) {
		this.id = id;
		this.followerUser = followerUser;
		this.followeeUser = followeeUser;
	}
}
