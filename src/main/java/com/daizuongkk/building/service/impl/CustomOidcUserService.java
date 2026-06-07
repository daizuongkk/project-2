package com.daizuongkk.building.service.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder encoder;

	@Override
	public OidcUser loadUser(OidcUserRequest request) {
		OidcUser oidcUser = super.loadUser(request);

		String providerId = oidcUser.getSubject();
		String email = oidcUser.getEmail();

		if (!StringUtils.hasText(email)) {
			throw new OAuth2AuthenticationException("Không lấy được email từ nhà cung cấp đăng nhập");
		}

		String fullName = StringUtils.hasText(oidcUser.getFullName())
				? oidcUser.getFullName()
				: email;

		User user = userRepository.findByGoogleAccountId(providerId)
				.orElseGet(() -> User.builder()
						.username(email)
						.password(encoder.encode(UUID.randomUUID().toString()))
						.active(true)
						.userRole(SystemConstant.USER_ROLE)
						.googleAccountId(providerId)
						.image(downloadGoogleAvatar(oidcUser.getPicture()))
						.build());

		user.setEmail(email);
		user.setFullName(fullName);

		userRepository.save(user);

		GrantedAuthority authority = new SimpleGrantedAuthority(user.getUserRole());

		return new DefaultOidcUser(
				Collections.singleton(authority),
				oidcUser.getIdToken(),
				oidcUser.getUserInfo(),
				"email");
	}

	private byte[] downloadGoogleAvatar(String imageUrl) {
		if (!StringUtils.hasText(imageUrl)) {
			return null;
		}

		try (InputStream inputStream = URI.create(imageUrl).toURL().openStream()) {
			return inputStream.readAllBytes();
		} catch (Exception e) {
			return null;
		}
	}
}
