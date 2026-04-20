package com.daizuongkk.building.model.dto.request;

import java.util.List;

import com.daizuongkk.building.model.dto.AbstractDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class BuildingDTO extends AbstractDTO {

	Long id;
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
	@Pattern(regexp = "^\\d+(,\\d+)*$", message = "rent area must be in the form \"100,200,300,..\"")
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
	String managerName;
	@Pattern(regexp = "^$|^\\d{10}$", message = "manager phone must have 10 digits")
	String managerPhone;
	@NotEmpty(message = "typecodes is required")
	List<String> typeCodes;
	Double brokerageFee;
	String note;
	String linkOfBuilding;
	String map;
	String image;
}
