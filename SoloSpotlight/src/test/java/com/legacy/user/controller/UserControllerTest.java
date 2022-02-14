package com.legacy.user.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.legacy.notify.dao.NotifyRepository;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.Role;
import com.legacy.user.domain.User;
import com.legacy.user.vo.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
	private final String USER_NAME = "유저_테스트";
	private Long userId;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	NotifyRepository notifyRepository;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mvc;
	private MockHttpSession session;
	
	@Before	
	public void setup() { 
		// 로그인 설정
		session = new MockHttpSession();
		User user = User.builder()
				.name(USER_NAME)
				.email(USER_NAME)
				.role(Role.USER)
				.build();
		System.out.println("[세션에 추가할 user] ->"+user.getName());
		userId = userRepository.save(user).getId();
		session.setAttribute("user", new SessionUser(user));
		
		// Mock 초기화
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	@After // Junit에서 단위 테스트가 끝날 때마다 수행
	public void cleanup() {
		userRepository.deleteById(userId);
		session.clearAttributes();
	}	
}
