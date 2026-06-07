package com.daizuongkk.building.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignCustomerDTO {

	@NotNull(message = "Mã khách hàng không được để trống")
	private Long customerId;

	private List<Long> staffIds;
}
