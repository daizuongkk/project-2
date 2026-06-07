package com.daizuongkk.building.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
	private String username;
	private String fullName;
	private String email;
	private String phone;
	private String role;
	private String image;

}
