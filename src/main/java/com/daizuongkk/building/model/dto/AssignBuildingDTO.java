package com.daizuongkk.building.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignBuildingDTO {

	@NotNull(message = "Mã tòa nhà không được để trống")
	private Long buildingId;

	private List<Long> staffIds;
}
