package com.daizuongkk.building.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import com.daizuongkk.building.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtils {

	@Value("${jwt.expiration}")
	private int expiration;

	@Value("${jwt.secret}")
	private String key;

	private SecretKey getSignInKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
	}

	public String generateToken(User user) {
		return Jwts.builder()
				.subject(user.getUsername())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
				.signWith(getSignInKey())
				.compact();
	}

	public Claims parseToken(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean isTokenExpired(String token) {
		return parseToken(token)
				.getExpiration()
				.before(new Date());
	}

	public String extractUsername(String token) {
		return parseToken(token).getSubject();
	}

	public boolean validateToken(String token, User user) {
		String username = extractUsername(token);
		return username.equals(user.getUsername()) && !isTokenExpired(token);
	}
}