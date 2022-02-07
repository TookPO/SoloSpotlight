package com.legacy.user.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final CustomOAuth2UserService customOAuth2UserService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http
				.csrf().disable() // csrf 토큰 비활성화 
				.headers().frameOptions().disable() // h2-conse 화면을 위해서
			.and()
				.authorizeRequests()
				.antMatchers("**/add").authenticated() // 이 주소로 들어오려면 인증이 필요
				.antMatchers("**/update").authenticated() // 이 주소로 들어오려면 인증이 필요
				.antMatchers("**/delete").authenticated() // 이 주소로 들어오려면 인증이 필요
				.antMatchers("/manager/**").access("hasRole('ROLE_MANAGER') or hasROLE('Role_BLOG_MANAGER')")
				.anyRequest().permitAll() // 위 주소가 아니라면 권한이 허용됨
			.and()
				.logout()
					.logoutSuccessUrl("/") // 로그아웃 시 url
			.and()
				.oauth2Login()
					.userInfoEndpoint() // 로그인 성공 이후 사용자 정보를 가져올때
						.userService(customOAuth2UserService); //
	}
	
}
