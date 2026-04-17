package com.daizuongkk.building.service.impl;

import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.repository.customrepo.impl.AccountRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final AccountRepository accountRepository;

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username);
		System.out.println("User= " + user);

		if (user == null) {
			throw new UsernameNotFoundException("User " //
					+ username + " was not found in the database");
		}

		// EMPLOYEE,MANAGER,..
		String role = user.getUserRole();

		List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();

		// ROLE_EMPLOYEE, ROLE_MANAGER
		GrantedAuthority authority = new SimpleGrantedAuthority(role);

		grantList.add(authority);

		boolean enabled = user.isActive();
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		return (UserDetails) new org.springframework.security.core.userdetails.User(user.getUserName(), //
				user.getEncrytedPassword(), enabled, accountNonExpired, //
				credentialsNonExpired, accountNonLocked, grantList);
	}
}
