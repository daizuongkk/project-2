package com.daizuongkk.building.model.dto.request;

import com.daizuongkk.building.model.dto.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO extends AbstractDTO {
	@NotBlank(message = "name is required")
	String name;

	@NotBlank(message = "district is required")
	String district;
	String ward;
	String street;
	String structure;

	@NotNull(message = "number of basement is required")
	Long numberOfBasement;

	@NotNull(message = "floor area is required")
	Long floorArea;

	String direction;

	@NotBlank(message = "rent area is required")
	@Pattern(regexp = "^\\d+(,\\d+)*$", message = "rent area is invalid")
	String rentArea;

    @NotNull(message = "district is required")
	@JsonProperty("rentPrice")
	Long price;
	String level;
	String rentPriceDescription;
	String serviceFee;
	String carFee;
	String motorbikeFee;
	String overtimeFee;
	String waterFee;
	String electricityFee;
	String deposit;
	String payment;
	String rentTime;
	String decorationTime;
	@NotBlank(message = "manager name is required")
	String managerName;
	@NotBlank(message = "manager phone is required")
	String managerPhone;
	@NotNull(message = "typecodes is required")
	@NotEmpty(message = "typecodes is required")
	List<String> typeCodes;
	Double brokerageFee;
	String note;
	String linkOfBuilding;
	String map;
	String image;
}
