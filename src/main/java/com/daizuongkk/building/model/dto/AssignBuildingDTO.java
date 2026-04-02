package com.daizuongkk.building.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignBuildingDTO {

	@NotNull(message = "building id is required")
	private Long buildingId;

	private List<Long> staffIds;
}
