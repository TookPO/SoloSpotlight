package com.legacy.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
	
	USER("ROLE_USER", "유저"),
	MANAGER("ROLE_MANAGER", "관리자"),
	BLOG_MANAGER("ROLE_BLOG_MANAGER", "블로그 매니저");
	
	private final String key;
	private final String title;
}
