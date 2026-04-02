package com.daizuongkk.building.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.daizuongkk.building.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional
@Repository
public class AccountRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public User findAccount(String userName) {
		return entityManager.find(User.class, userName);
	}
}
