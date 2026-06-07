package com.daizuongkk.building.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ContactRequest {
	@NotBlank(message = "Họ và tên không được để trống")
	private String fullName;
	private String email;
	@NotBlank(message = "Số điện thoại không được để trống")
	private String phone;
	private String demand;
}
