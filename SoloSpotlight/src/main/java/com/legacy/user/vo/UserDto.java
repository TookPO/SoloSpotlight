package com.legacy.user.vo;

import com.legacy.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
// @AllArgsConstructor
public class UserDto {
	private Long id;
	private String name;
	private String email;
	private String picture;
	
	@Builder
	public UserDto(Long id, String name, String email, String picture) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.picture = picture;
	}
	
	public UserDto(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
	}
}
