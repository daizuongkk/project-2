package com.daizuongkk.building.repository.customrepo.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.model.dto.request.SearchCustomerRequest;
import com.daizuongkk.building.pagination.PaginationResult;
import com.daizuongkk.building.repository.customrepo.CustomCustomerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomCustomerRepositoryImpl implements CustomCustomerRepository {

	private final EntityManager entityManager;

	@Override
	public PaginationResult<Customer> searchCustomers(SearchCustomerRequest request, int page, int size, int maxNavPage) {

		StringBuilder sql = new StringBuilder("""
				SELECT DISTINCT c.*
				FROM customer c
				""");

		Map<String, Object> params = new HashMap<>();

		if (request.getStaffId() != null) {
			sql.append(" JOIN assignmentcustomer ac ON ac.customerid = c.id");
		}

		sql.append(" WHERE 1=1 AND c.is_active");

		if (StringUtils.hasText(request.getName())) {
			sql.append(" AND LOWER(c.fullname) LIKE LOWER(:name)");
			params.put("name", "%" + request.getName().trim() + "%");
		}

		if (StringUtils.hasText(request.getEmail())) {
			sql.append(" AND LOWER(c.email) LIKE LOWER(:email)");
			params.put("email", "%" + request.getEmail().trim() + "%");
		}

		if (StringUtils.hasText(request.getPhone())) {
			sql.append(" AND c.phone LIKE :phone");
			params.put("phone", "%" + request.getPhone().trim() + "%");
		}

		if (StringUtils.hasText(request.getStatus())) {
			sql.append(" AND c.status = :status");
			params.put("status", request.getStatus());
		}

		if (request.getStaffId() != null) {
			sql.append(" AND ac.staffid = :staffId");
			params.put("staffId", request.getStaffId());
		}

		sql.append(" ORDER BY createddate DESC");
		Query query = entityManager.createNativeQuery(sql.toString(), Customer.class);

		params.forEach(query::setParameter);

		return new PaginationResult<>(
				query,
				query.getResultList().size(),
				page,
				size,
				maxNavPage);
	}
}