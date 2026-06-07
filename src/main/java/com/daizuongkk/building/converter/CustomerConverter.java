package com.daizuongkk.building.converter;

import javax.swing.text.html.parser.Entity;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.enums.CustomerStatus;
import com.daizuongkk.building.model.dto.CustomerDTO;
import com.daizuongkk.building.model.dto.response.CustomerResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerConverter {
	private final ModelMapper mapper;

	public CustomerResponse entityToResponse(Customer customer) {
		CustomerResponse customerResponse = mapper.map(customer, CustomerResponse.class);
		customerResponse.setName(customer.getFullName());
		customerResponse.setStatus(CustomerStatus.valueOf(customer.getStatus()).getName());
		return customerResponse;

	}

	public Customer dtoToEntity(CustomerDTO customerDTO) {
		Customer customer = mapper.map(customerDTO, Customer.class);
		customer.setFullName(customerDTO.getName());
		return customer;
	}

	public CustomerDTO entityToDTO(Customer customer) {
		CustomerDTO customerDTO = mapper.map(customer, CustomerDTO.class);
		customerDTO.setName(customer.getFullName());
		return customerDTO;
	}

}
