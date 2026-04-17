package com.daizuongkk.building.builder;

import java.util.List;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class BuildingSearchBuilder {
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
	String managerPhone;
	Long staffId;
	List<String> typeCodes;

}
