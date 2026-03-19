package com.daizuongkk.building.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daizuongkk.building.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserName(String userName);

	void deleteByIdIn(List<Long> ids);
}
