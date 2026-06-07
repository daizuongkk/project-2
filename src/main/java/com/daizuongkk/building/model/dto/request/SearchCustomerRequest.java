package com.daizuongkk.building.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCustomerRequest {

	private String name;
	private String email;
	private String phone;
	private Long staffId;
	private String status;
}
