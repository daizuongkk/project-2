package com.daizuongkk.building.repository.customrepo;

import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.model.dto.request.SearchCustomerRequest;
import com.daizuongkk.building.pagination.PaginationResult;


public interface CustomCustomerRepository {

	PaginationResult<Customer> searchCustomers(SearchCustomerRequest request, int page, int size, int maxNavPage);
}
