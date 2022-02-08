package com.legacy.user.vo;

import java.io.Serializable;

import com.legacy.user.domain.Role;
import com.legacy.user.domain.User;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
	private Long id;
	private String name;
	private String email;
	private String picture;
	private Role role;
	private int age;
	private String location;
	private String job;
	private int prohibit;
	
	public SessionUser(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
		this.role = user.getRole();
		this.age = user.getAge();
		this.location = user.getLocation();
		this.job = user.getJob();
		this.prohibit = user.getProhibit();
	}
	
}
