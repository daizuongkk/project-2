// package com.daizuongkk.building.config;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.util.Pair;
// import
// org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import
// org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.daizuongkk.building.entity.User;
// import com.daizuongkk.building.utils.JwtUtils;

// import io.micrometer.common.lang.NonNull;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;

// @Configuration
// @RequiredArgsConstructor
// public class JwtFilter extends OncePerRequestFilter {

// private final JwtUtils jwtUtils;
// private final UserDetailsService userDetailsService;

// @SuppressWarnings("null")
// @Override
// protected void doFilterInternal(HttpServletRequest request,
// HttpServletResponse response, FilterChain filterChain)
// throws ServletException, IOException {
// try {
// if (isBypassToken(request)) {
// filterChain.doFilter(request, response);
// return;
// }

// final String authHeader = request.getHeader("Authorization");

// if (authHeader == null || !authHeader.startsWith("Bearer ")) {
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
// return;
// }

// final String token = authHeader.substring(7);
// final String username = jwtUtils.extractUsername(token);

// if (username != null
// && SecurityContextHolder.getContext().getAuthentication() == null) {
// User userDetails = (User) userDetailsService.loadUserByUsername(username);
// if (jwtUtils.validateToken(token, userDetails)) {
// UsernamePasswordAuthenticationToken authenticationToken = new
// UsernamePasswordAuthenticationToken(
// userDetails,
// null,
// userDetails.getAuthorities());
// authenticationToken.setDetails(new
// WebAuthenticationDetailsSource().buildDetails(request));
// SecurityContextHolder.getContext().setAuthentication(authenticationToken);
// }
// }

// } catch (Exception e) {
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
// }
// }

// private boolean isBypassToken(@NonNull HttpServletRequest request) {
// final List<Pair<String, String>> bypassTokens = Arrays.asList(
// Pair.of("admin/login", "POST"),
// Pair.of("register", "POST"));

// for (Pair<String, String> bypassToken : bypassTokens) {
// if (request.getServletPath().contains(bypassToken.getFirst())
// && request.getServletPath().contains(bypassToken.getSecond())) {
// return true;
// }
// }
// return false;
// }
// }
