package com.daizuongkk.building.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.daizuongkk.building.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String userName);

	void deleteByIdIn(List<Long> ids);

	List<User> findByUserRoleAndActiveTrue(String role);

	List<User> findByIdIn(List<Long> staffIds);

	boolean existsByUsername(String username);

	Optional<User> findByEmail(String email);

	Optional<User> findByGoogleAccountId(String providerId);

	Optional<User> findByFacebookAccountId(String providerId);
}
