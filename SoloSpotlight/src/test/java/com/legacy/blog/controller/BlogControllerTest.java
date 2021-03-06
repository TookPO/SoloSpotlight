package com.legacy.blog.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legacy.blog.category.dao.BlogCategoryRepository;
import com.legacy.blog.category.domain.BlogCategory;
import com.legacy.blog.info.dao.BlogInfoRepository;
import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.info.vo.BlogInfoDto;
import com.legacy.blog.post.dao.BlogPostRepository;
import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.reply.domain.BlogReply;
import com.legacy.blog.reply.repository.BlogReplyRepository;
import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.Role;
import com.legacy.user.domain.User;
import com.legacy.user.vo.SessionUser;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BlogControllerTest {
	private final String USER_NAME = "?????????_?????????";
	private Long userId;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BlogCategoryRepository blogCategoryRepository;
	
	@Autowired
	BlogInfoRepository blogInfoRepository;
	
	@Autowired
	BlogPostRepository blogPostRepository;
	
	@Autowired
	BlogReplyRepository blogReplyRepository;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mvc; // ????????? ??????????????? ?????? ?????? ?????????????????? MVC ????????? ?????? HTTP ????????? ????????? ??????
	private MockHttpSession session;

	@Before // ?????? ?????? ????????? ?????? ??? mock ?????????
	public void setup() {
		// ????????? ??????
		session = new MockHttpSession();
		User user = User.builder()
				.name(USER_NAME)
				.email(USER_NAME)
				.role(Role.USER)
				.build();
		System.out.println("[????????? ????????? user] ->"+user.getName());
		userId = userRepository.save(user).getId();
		session.setAttribute("user", new SessionUser(user));
		
		// Mock ?????????
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	@After // Junit?????? ?????? ???????????? ?????? ????????? ??????
	public void cleanup() {
		blogReplyRepository.deleteAll();
		blogPostRepository.deleteAll();
		blogCategoryRepository.deleteAll();
		blogInfoRepository.deleteAll();
		userRepository.deleteById(userId);
		session.clearAttributes();
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void BlogInfo_?????????_BlogCategory_????????????() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};
		Map<String, Object> userDto = new HashMap<>(); // ??????????????? ?????????
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
		BlogInfo oneInfo = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		List<String> allCategory = blogCategoryRepository.findByBlogInfoIdString(oneInfo.getId()); 
		assertThat(oneInfo.getName()).isEqualTo(name);
		System.out.println("[?????????]->"+oneInfo.getCreatedDate());
		int i=0;
		for(String title: category) {
			assertThat(allCategory.get(i++)).isEqualTo(title);
		}
	}
	
	@Test
	@WithMockUser(roles="USER")
	public void BlogInfo_?????????_??????() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};		
		User user = userRepository.findById(userId).get();
		System.out.println("[????????? ?????????]->"+userId);
		Long infoId = blogInfoRepository.save(BlogInfo.builder()
				.name(name)
				.intro(intro)
				.headerColor(headerColor)
				.revenue(0L)
				.user(user)
				.build()).getId();
		System.out.println("[???????????? info] ="+infoId);
		
		Map<String, Object> userDto = new HashMap<>(); // ??????????????? ?????????
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
	public void BlogInfo_?????????_BlogCategory_????????????() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String[] category = {"category1", "category2"};
		List<String> categoryList = Arrays.asList(category);
		
		User user = userRepository.findById(userId).get();
		System.out.println("[????????? ?????????]->"+userId);
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
		
		System.out.println("????????? ???????????? ??? ->"+blogCategoryRepository.findByBlogInfoIdString(blogInfo.getId()));
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
		BlogInfo blogInfo2 = blogInfoRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		System.out.println("[?????? ??????] ->"+blogInfo.getName());
		List<String> blogCategoryList = blogCategoryRepository.findByBlogInfoIdString(blogInfo2.getId());
		blogCategoryList.forEach(result -> System.out.println("[???????????? ??????] -> "+ result));
		assertThat(blogInfo2.getName()).isEqualTo(expectedName);
		assertThat(blogInfo2.getIntro()).isEqualTo(expectedIntro);
		assertThat(blogInfo2.getHeaderColor()).isEqualTo(expectedHeaderColor);
		int i = 0;
		for(String result: expectedCategory) {
			System.out.println(blogCategoryList+" to "+result);
			assertThat(blogCategoryList.get(i++)).isEqualTo(result);
		}
	}
	
	@Test
	@WithMockUser(roles="USER")
	@Transactional
	public void BlogPost_????????????() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String category = "category";
		String isPublic = "true";
		String isRecommend = "true";
		Long blogCategoryId = 0L;
		String title = "title";
		String thumbnail = "thumbnail";
		String content = "content";
		
		User user = userRepository.findById(userId).get();
		System.out.println("[????????? ?????????]->"+userId);
		BlogInfo blogInfo = BlogInfo.builder()
				.name(name)
				.intro(intro)
				.headerColor(headerColor)
				.revenue(0L)
				.user(user)
				.build();
		blogInfoRepository.save(blogInfo);
		blogCategoryId = blogCategoryRepository.save(BlogCategory.builder()
				.title(category)
				.blogInfo(blogInfo)
				.build()).getId();
		
		Map<String, Object> data = new HashMap<>();
		data.put("isPublic", isPublic);
		data.put("isRecommend", isRecommend);
		data.put("blogCategoryId", blogCategoryId);
		data.put("title", title);
		data.put("thumbnail", thumbnail);
		data.put("content", content);
		
		String url ="http://localhost:"+port+"/blog/"+userId+"/add";
		
		// when
		mvc.perform(post(url)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(data)))
				.andExpect(status().isOk());
		
		// than
		BlogPost blogPost = blogPostRepository.findByInfoId(blogInfo.getId())
				.orElseThrow(() -> new IllegalArgumentException("???????????? ????????????."));
		System.out.println("[????????? POST]->"+blogPost.getTitle());
		assertThat(blogPost.getTitle()).isEqualTo(title);
		assertThat(blogPost.getThumbnail()).isEqualTo(thumbnail);		
		assertThat(blogPost.getContent()).isEqualTo(content);
		System.out.println("[???????????? assert]->"+blogPost.getBlogCategory().getTitle());
		assertThat(blogPost.getBlogCategory().getTitle()).isEqualTo(category);
		assertThat(blogPost.getBlogInfo().getName()).isEqualTo(name);
	}
	
	@Test
	@WithMockUser(roles ="USER")
	@Transactional
	public void ??????_????????????() throws Exception {
		// given
		String name = "name";
		String intro = "intro";
		String headerColor = "hdColor";
		String category = "category";
		String title = "title";
		String thumbnail = "thumbnail";
		String content = "content";
		
		User user = userRepository.findById(userId).get();
		System.out.println("[????????? ?????????]->"+userId);
		BlogInfo blogInfo = BlogInfo.builder()
				.name(name)
				.intro(intro)
				.headerColor(headerColor)
				.revenue(0L)
				.user(user)
				.build();
		blogInfoRepository.save(blogInfo);
		BlogCategory blogCategory = blogCategoryRepository.save(BlogCategory.builder()
				.title(category)
				.blogInfo(blogInfo)
				.build());
		Long postId = blogPostRepository.save(BlogPost.builder()
				.title(title)
				.content(content)
				.thumbnail(thumbnail)
				.viewCount(0L)
				.isPublic(true)
				.isRecommend(true)
				.blogInfo(blogInfo)
				.blogCategory(blogCategory)
				.build()).getId();
		Map<String, Object> data = new HashMap<>();
		data.put("postId", postId);
		data.put("content", content);

		String url ="http://localhost:"+port+"/blog/"+userId+"/reply/add";
		
		// when
		mvc.perform(post(url)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsString(data)))
				.andExpect(status().isOk());
		
		// then
		List<BlogReply> blogReply = blogReplyRepository.findAll();
		assertThat(blogReply.get(0).getContent()).isEqualTo(content);		
	}
}
