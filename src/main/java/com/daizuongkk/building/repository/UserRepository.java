package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daizuongkk.building.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserName(String userName);

	void deleteByIdIn(List<Long> ids);

	List<User> findByUserRoleAndActiveTrue(String role);

	List<User> findByIdIn(List<Long> staffIds);
}
