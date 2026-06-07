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
	@NotBlank(message = "Tên tòa nhà không được để trống")
	String name;

	@NotBlank(message = "Vui lòng chọn quận")
	String district;
	String ward;
	String street;
	String structure;

	@NotNull(message = "Số tầng hầm không được để trống")
	Long numberOfBasement;

	@NotNull(message = "Diện tích sàn không được để trống")
	Long floorArea;

	String direction;

	@NotBlank(message = "Diện tích thuê không được để trống")
	@Pattern(regexp = "^\\d+(,\\d+)*$", message = "Diện tích thuê phải có dạng \"100,200,300,...\"")
	String rentArea;

	@NotNull(message = "Giá thuê không được để trống")
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
	@Pattern(regexp = "^$|^\\d{10}$", message = "Số điện thoại quản lý phải có 10 chữ số")
	String managerPhone;
	@NotEmpty(message = "Vui lòng chọn ít nhất một loại tòa nhà")
	List<String> typeCodes;
	Double brokerageFee;
	String note;
	String linkOfBuilding;
	String map;
	String image;
	String imageType;
}
