package com.legacy.notify.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.legacy.domain.BaseTimeEntity;
import com.legacy.user.domain.User;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Notify extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String division;
	
	@Column(nullable = false)
	private String message;
	
	@Column(nullable = false)
	private String information;
	
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY) // 기본값이 EAGER
	@JoinColumn(name="USER_ID")
	private User user;
	
	@Builder
	public Notify(Long id, String division, String message, String information, User user) {
		this.id = id;
		this.division = division;
		this.message = message;
		this.information = information;
		this.user = user;
	}
}
