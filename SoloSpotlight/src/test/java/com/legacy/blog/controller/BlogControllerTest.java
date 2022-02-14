package com.legacy.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.Role;
import com.legacy.user.domain.User;
import com.legacy.user.vo.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BlogControllerTest {
	private final String USER_NAME = "블로그_테스트";
	private Long userId;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BlogCategoryRepository blogCategoryRepository;
	
	@Autowired
	BlogInfoRepository blogInfoRepository;	
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mvc; // 서블릿 컨테이너의 구동 없이 시뮬레이션된 MVC 환경에 모의 HTTP 서블릿 요청을 전송
	private MockHttpSession session;

	@Before // 하기 전에 로그인 설정 및 mock 초기화
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
		blogCategoryRepository.deleteAll();
		blogInfoRepository.deleteAll();
		userRepository.deleteById(userId);
		session.clearAttributes();
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void UserInfo_그리고_BlogCategory_등록된다() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};
		Map<String, Object> userDto = new HashMap<>(); // 다이아몬드 연산자
		userDto.put("name", name);
		userDto.put("intro", intro);
		userDto.put("headerColor", headerColor);
		userDto.put("category", category);
		
		String url ="http://localhost:"+port+"/blog/add";
		
		// when
		mvc.perform(post(url)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDto)))
				.andExpect(status().isOk());
		
		// then 
		List<BlogInfo> allInfo = blogInfoRepository.findAll();
		List<BlogCategory> allCategory = blogCategoryRepository.findAll(); 
		allInfo.forEach(one -> assertThat(one.getName()).isEqualTo(name));
		System.out.println("[생성일]->"+allInfo.get(0).getCreatedDate());
		int i=0;
		for(String title: category) {
			assertThat(allCategory.get(i++).getTitle()).isEqualTo(title);
		}
	}
}
