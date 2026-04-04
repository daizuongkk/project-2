package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.repository.AccountRepository;
import com.daizuongkk.building.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

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
