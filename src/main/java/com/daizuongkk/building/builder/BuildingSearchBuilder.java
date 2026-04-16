package com.daizuongkk.building.builder;

import java.util.List;

import com.daizuongkk.building.model.dto.AbstractDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchBuilder extends AbstractDTO {
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
