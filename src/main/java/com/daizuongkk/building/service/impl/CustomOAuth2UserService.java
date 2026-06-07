package com.daizuongkk.building.service.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(request);

		String provider = request.getClientRegistration().getRegistrationId();

		if (!"facebook".equals(provider)) {
			return oauth2User;
		}

		String providerId = oauth2User.getAttribute("id");
		String email = oauth2User.getAttribute("email");
		String fullName = oauth2User.getAttribute("name");

		if (!StringUtils.hasText(email)) {
			email = "facebook_" + providerId + "@oauth.local";
		}

		if (!StringUtils.hasText(fullName)) {
			fullName = email;
		}

		final String finalEmail = email;
		User user = userRepository.findByFacebookAccountId(providerId)
				.orElseGet(() -> User.builder()
						.username(finalEmail)
						.password(encoder.encode(UUID.randomUUID().toString()))
						.active(true)
						.userRole(SystemConstant.USER_ROLE)
						.facebookAccountId(providerId)
						.image(downloadFacebookAvatar(providerId))
						.build());

		user.setEmail(email);
		user.setFullName(fullName);

		userRepository.save(user);

		GrantedAuthority authority = new SimpleGrantedAuthority(user.getUserRole());

		Map<String, Object> attributes = oauth2User.getAttributes();

		return new DefaultOAuth2User(
				Collections.singleton(authority),
				attributes,
				"email");
	}

	private byte[] downloadFacebookAvatar(String providerId) {
		if (!StringUtils.hasText(providerId)) {
			return null;
		}

		String imageUrl = "https://graph.facebook.com/" + providerId + "/picture?type=large";

		try (InputStream inputStream = URI.create(imageUrl).toURL().openStream()) {
			return inputStream.readAllBytes();
		} catch (Exception e) {
			return null;
		}
	}
}