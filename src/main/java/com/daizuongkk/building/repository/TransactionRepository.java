package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.daizuongkk.building.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	List<Transaction> findByCustomer_IdAndCode(Long customerId, String type);

	@Modifying
	@Query("""
			    UPDATE Transaction t
			    SET t.isActive = false
			    WHERE t.id IN :ids
			""")
	void deactivateTransactions(List<Long> ids);

	List<Transaction> findByCustomer_IdAndIsActiveAndCode(Long customerId, boolean isActive, String type);

	List<Transaction> findAllByCustomer_IdIn(List<Long> customerIds);
}
