package com.legacy.user.config;

import java.util.Collections;


import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.legacy.user.dao.UserRepository;
import com.legacy.user.domain.User;
import com.legacy.user.vo.OAuthAttributes;
import com.legacy.user.vo.SessionUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final UserRepository userRepository;
	private final HttpSession httpSession;
	private static Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser((OAuth2UserRequest) userRequest);
		String registrationId = ((OAuth2UserRequest) userRequest)
				.getClientRegistration().getRegistrationId();
		
		String userNameAttributeName = ((OAuth2UserRequest) userRequest).
				getClientRegistration().getProviderDetails()
				.getUserInfoEndpoint().
							getUserNameAttributeName();
		
		OAuthAttributes attributes = OAuthAttributes.
				of(registrationId, userNameAttributeName,
				oAuth2User.getAttributes());
							
		User user = saveOrUpdate(attributes);
		
		httpSession.setAttribute("user", new SessionUser(user));

		return new DefaultOAuth2User(
				Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
				attributes.getAttributes(),
				attributes.getNameAttributeKey());
	}
	
	private User saveOrUpdate(OAuthAttributes attributes) {
		logger.debug("=======이메일 찾기======="+attributes.getEmail());
		User user = userRepository.findByEmail(attributes.getEmail())
				.map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
				.orElse(attributes.toEntity());
		
		return userRepository.save(user);
	}

}
