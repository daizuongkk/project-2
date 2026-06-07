package com.daizuongkk.building.config;

import com.daizuongkk.building.security.CustomSuccessHandler;
import com.daizuongkk.building.service.impl.CustomOAuth2UserService;
import com.daizuongkk.building.service.impl.CustomOidcUserService;
import com.daizuongkk.building.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private static final String MANAGER = "MANAGER";
	private static final String STAFF = "STAFF";

	private final UserDetailsServiceImpl userDetailsService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			CustomOidcUserService oidcUserService,
			CustomOAuth2UserService customOAuth2UserService) throws Exception {

		http
				.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.POST, "/api/buildings").hasRole(MANAGER)
						.requestMatchers(HttpMethod.PUT, "/api/buildings").hasAnyRole(MANAGER, STAFF)
						.requestMatchers("/api/buildings/assign").hasRole(MANAGER)
						.requestMatchers(HttpMethod.GET, "/api/buildings/*/staffs").hasRole(MANAGER)
						.requestMatchers(HttpMethod.DELETE, "/api/buildings/**").hasRole(MANAGER)
						.requestMatchers("/api/buildings/**").hasAnyRole(MANAGER, STAFF)

						.requestMatchers(HttpMethod.GET, "/admin/users/userImage").hasAnyRole(MANAGER, STAFF)
						.requestMatchers("/admin/users/list").hasRole(MANAGER)
						// .requestMatchers("/admin/users/**").hasRole(MANAGER)
						.requestMatchers("/api/users/register").permitAll()
						// .requestMatchers("/api/users").hasRole(MANAGER)
						.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole(MANAGER)
						.requestMatchers(HttpMethod.POST, "/api/users/**").hasRole(MANAGER)

						.requestMatchers(HttpMethod.POST, "/api/customers").permitAll()
						.requestMatchers(HttpMethod.PUT, "/api/customers").hasAnyRole(MANAGER, STAFF)
						.requestMatchers("/api/customers/assign").hasRole(MANAGER)
						.requestMatchers(HttpMethod.GET, "/api/customers/*/staffs").hasRole(MANAGER)
						.requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole(MANAGER)
						.requestMatchers("/api/customers/**").hasAnyRole(MANAGER, STAFF)

						.requestMatchers("/admin/customers/list/**").hasAnyRole(MANAGER, STAFF)
						.requestMatchers(HttpMethod.DELETE, "/admin/customers/**").hasRole(MANAGER)

						.requestMatchers("/admin/buildings/create").hasRole(MANAGER)
						.requestMatchers("/admin/building").hasRole(MANAGER)

						.requestMatchers(HttpMethod.DELETE, "/api/transactions/**").hasRole(MANAGER)
						.requestMatchers("/api/transactions").hasAnyRole(MANAGER, STAFF)
						.requestMatchers("/api/transactions/**").hasAnyRole(MANAGER, STAFF)

						.requestMatchers("/admin/**").hasAnyRole(MANAGER, STAFF)

						.anyRequest().permitAll())

				.exceptionHandling(ex -> ex.accessDeniedPage("/403"))

				.formLogin(form -> form
						.loginPage("/admin/login")
						.loginProcessingUrl("/j_spring_security_check")
						.successHandler(myAuthenticationSuccessHandler())
						.failureUrl("/admin/login?incorrectAccount")
						.usernameParameter("userName")
						.passwordParameter("password")
						.permitAll())

				.oauth2Login(oauth2 -> oauth2
						.loginPage("/admin/login")
						.userInfoEndpoint(info -> info.oidcUserService(oidcUserService).userService(customOAuth2UserService))
						.successHandler(myAuthenticationSuccessHandler())
						.failureUrl("/admin/login?incorrectAccount")
						.permitAll())

				.logout(logout -> logout
						.logoutUrl("/admin/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}

	@Bean
	public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
		return new CustomSuccessHandler();
	}
}
