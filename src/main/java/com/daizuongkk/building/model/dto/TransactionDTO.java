package com.daizuongkk.building.model.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TransactionDTO {
	private Long id;
	@NotBlank(message = "Loại giao dịch không được để trống")
	private String code;
	private String note;
	@NotNull(message = "Mã khách hàng không được để trống")
	private Long customerId;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date modifiedDate;
}
