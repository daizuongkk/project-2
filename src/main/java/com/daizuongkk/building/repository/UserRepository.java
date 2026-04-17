package com.daizuongkk.building.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserName(String userName);

	void deleteByIdIn(List<Long> ids);

	List<User> findByUserRoleAndActiveTrue(String role);

	List<User> findByAssignmentBuilding_Building(Building building);

	List<User> findByIdIn(List<Long> staffIds);
}
