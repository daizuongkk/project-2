package com.daizuongkk.building.utils;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUtils {
	private AuthUtils() {
		/* This utility class should not be instantiated */
	}

	public static List<String> getAuthorities() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return List.of();
		}
		return authentication.getAuthorities().stream().map(auth -> auth.getAuthority()).toList();
	}

	public static UserDetails getCurrentUser() {

		return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
