package com.daizuongkk.building.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	@NotBlank(message = "Tên đăng nhập không được để trống")
	@Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 ký tự")
	private String username;
	@NotBlank(message = "Họ và tên không được để trống")

	private String fullName;
	@NotBlank(message = "Mật khẩu không được để trống")
	@Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
	private String password;
	@NotBlank(message = "Mật khẩu xác nhận không được để trống")
	private String confirmPassword;
}
