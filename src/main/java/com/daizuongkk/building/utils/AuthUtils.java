package com.daizuongkk.building.utils;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class AuthUtils {

	private AuthUtils() {
	}

	public static List<String> getAuthorities() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return List.of();
		}

		return authentication.getAuthorities()
				.stream()
				.map(auth -> auth.getAuthority())
				.toList();
	}

	public static String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return null;
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetails userDetails) {
			return userDetails.getUsername();
		}

		if (principal instanceof OidcUser oidcUser) {
			return oidcUser.getEmail();
		}

		return authentication.getName();
	}
}