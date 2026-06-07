package com.daizuongkk.building.model.dto.response;

import java.util.Date;

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
public class CustomerResponse {
	private Long id;
	private String name;
	private String phone;
	private String email;
	private String demand;
	private String createdBy;
	private Date createdDate;
	private String status;

}
