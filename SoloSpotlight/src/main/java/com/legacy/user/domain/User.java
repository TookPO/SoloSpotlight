package com.legacy.user.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.legacy.domain.BaseTimeEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length=30)
	private String name;
	
	@Column(nullable = false, length=320)
	private String email;
	
	@Column(length=2400)
	private String picture;
	
	@Enumerated(EnumType.STRING) // 기본은 int
	@Column(nullable = false, length=12)
	private Role role;
	
	@Column
	private int age;
	
	@Column(length=100)
	private String location;
	
	@Column
	private String job;
	
	@Column
	private int prohibit;

	@Builder
	public User(Long id, String name, String email, String picture, Role role, int age, String location, String job,
			int prohibit) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
		this.role = role;
		this.age = age;
		this.location = location;
		this.job = job;
		this.prohibit = prohibit;
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
