package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.repository.customrepo.CustomCustomerRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomCustomerRepository {

	boolean existsByPhone(String phone);

	@Modifying
	@Query("""
			    UPDATE Customer c
			    SET c.isActive = false
			    WHERE c.id IN :ids
			""")
	void deactivateCustomers(@Param("ids") List<Long> ids);

}
