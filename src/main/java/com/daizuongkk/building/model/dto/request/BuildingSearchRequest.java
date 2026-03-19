package com.daizuongkk.building.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchRequest {
	String name;
	String ward;
	String street;
	String district;
	Long floorArea;
	Long numberOfBasement;
	String direction;
	String level;
	Long minRentArea;
	Long maxRentArea;
	Long minRentPrice;
	Long maxRentPrice;
	String managerName;
	String managerPhoneNumber;
	Long staffId;
	List<String> typeCodes;

}
