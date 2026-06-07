package com.daizuongkk.building.service;

import java.util.List;

import com.daizuongkk.building.model.dto.AssignCustomerDTO;
import com.daizuongkk.building.model.dto.CustomerDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.SearchCustomerRequest;
import com.daizuongkk.building.model.dto.response.CustomerResponse;
import com.daizuongkk.building.pagination.PaginationResult;

public interface CustomerService {

	void addCustomer(CustomerDTO request);

	PaginationResult<CustomerResponse> findCustomers(SearchCustomerRequest request, int page, int size, int maxNavPage);

	ResponseDTO loadStaff(Long customerId);

	void assignCustomer(AssignCustomerDTO assignCustomerDTO);

	void deleteCustomers(List<Long> ids);

	CustomerDTO findById(Long id);

	CustomerDTO updateCustomer(CustomerDTO customerDTO);

}
