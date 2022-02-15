package com.legacy.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.info.vo.BlogInfoDto;
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
	public void BlogInfo_그리고_BlogCategory_등록된다() throws Exception {
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
	
	@Test
	@WithMockUser(roles="USER")
	public void BlogInfo_하나만_조회() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};		
		User user = userRepository.findById(userId).get();
		System.out.println("[조회할 아이디]->"+userId);
		Long infoId = blogInfoRepository.save(BlogInfo.builder()
				.name(name)
				.intro(intro)
				.headerColor(headerColor)
				.revenue(0L)
				.user(user)
				.build()).getId();
		System.out.println("[만들어진 info] ="+infoId);
		
		Map<String, Object> userDto = new HashMap<>(); // 다이아몬드 연산자
		userDto.put("name", name);
		userDto.put("intro", intro);
		userDto.put("headerColor", headerColor);
		userDto.put("category", category);
		
		String url ="http://localhost:"+port+"/blog/"+userId;		
		
		// when
		MvcResult mvcResult = mvc.perform(get(url)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDto)))
				.andExpect(status().isOk())
				.andReturn();
		
		// then
		ModelAndView mav = mvcResult.getModelAndView();
		BlogInfoDto blogInfoDto = (BlogInfoDto) mav.getModelMap().get("blogInfoDto");
		System.out.println("[INFO-DTO]->"+blogInfoDto);
		assertThat(blogInfoDto.getName()).isEqualTo(name);
		assertThat(blogInfoDto.getIntro()).isEqualTo(intro);
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void BlogInfo_그리고_BlogCategory_수정한다() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};
		List<String> categoryList = Arrays.asList(category);
		
		User user = userRepository.findById(userId).get();
		System.out.println("[조회할 아이디]->"+userId);
		BlogInfo blogInfo = BlogInfo.builder()
				.name(name)
				.intro(intro)
				.headerColor(headerColor)
				.revenue(0L)
				.user(user)
				.build();
		blogInfoRepository.save(blogInfo);
		blogCategoryRepository.save(BlogCategory.builder()
				.title(category[0])
				.blogInfo(blogInfo)
				.build());
		blogCategoryRepository.save(BlogCategory.builder()
				.title(category[1])
				.blogInfo(blogInfo)
				.build());
		
		System.out.println("넣어줄 카테고리 값 ->"+blogCategoryRepository.findByBlogInfoIdString(blogInfo.getId()));
		String expectedName = "name1";
		String expectedIntro = "intro1";
		String expectedHeaderColor = "HColor1";
		String[] expectedCategory = {"category3", "category4", "category5", "category6"};
		Map<String, Object> userDto = new HashMap<>();
		userDto.put("name", expectedName);
		userDto.put("intro", expectedIntro);
		userDto.put("headerColor", expectedHeaderColor);
		userDto.put("category", expectedCategory);
		
		String url ="http://localhost:"+port+"/blog/update";
		
		// when
		mvc.perform(put(url)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(userDto)))
				.andExpect(status().isOk());
		
		// than
		BlogInfo blogInfo2 = blogInfoRepository.findByUserId(userId).get();
		System.out.println("[정보 결과] ->"+blogInfo.getName());
		List<String> blogCategoryList = blogCategoryRepository.findByBlogInfoIdString(blogInfo2.getId());
		blogCategoryList.forEach(result -> System.out.println("[카테고리 결과] -> "+ result));
		assertThat(blogInfo2.getName()).isEqualTo(expectedName);
		assertThat(blogInfo2.getIntro()).isEqualTo(expectedIntro);
		assertThat(blogInfo2.getHeaderColor()).isEqualTo(expectedHeaderColor);
		int i = 0;
		for(String result: expectedCategory) {
			System.out.println(blogCategoryList+" to "+result);
			assertThat(blogCategoryList.get(i++)).isEqualTo(result);
		}
	}
}
