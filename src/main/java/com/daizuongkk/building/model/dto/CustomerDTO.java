package com.daizuongkk.building.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {

	private Long id;

	@NotBlank(message = "Tên khách hàng không được để trống")
	private String name;

	@NotBlank(message = "Số điện thoại không được để trống")
	@Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0")
	private String phone;

	private String email;

	private String company;

	private String demand;

	@NotBlank(message = "Trạng thái không được để trống")
	private String status;
}