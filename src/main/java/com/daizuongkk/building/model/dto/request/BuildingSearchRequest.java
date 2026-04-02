package com.daizuongkk.building.model.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import com.daizuongkk.building.model.dto.AbstractDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchRequest extends AbstractDTO {
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
