package com.daizuongkk.building.model.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingResponse {
	Long id;
	String name;
	String address;
	Long numberOfBasement;
	String managerName;
	String managerPhone;
	Long floorArea;
	String rentArea;
	Long emptyArea;
	Long rentPrice;
	String serviceFee;
	Double brokerageFee;
}
