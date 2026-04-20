package com.daizuongkk.building.converter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.enums.District;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuildingConverter {

	private final ModelMapper mapper;

	public BuildingResponse entityToResponse(Building building) {

		BuildingResponse buildingResponse = mapper.map(building, BuildingResponse.class);

		buildingResponse.setAddress(building.getStreet() + ", " + building.getWard() + ", "
				+ (building.getDistrict() != null
						? District.valueOf(building.getDistrict()).getName()
						: ""));

		buildingResponse.setRentArea(
				building.getRentArea().stream().map(r -> r.getValue().toString()).collect(Collectors.joining(", ")));
		return buildingResponse;
	}

	public Building dtoToEntity(BuildingDTO buildingDTO) {
		Building building = mapper.map(buildingDTO, Building.class);
		String typeCodes = String.join(",", buildingDTO.getTypeCodes());

		building.setType(typeCodes);

		return building;
	}

	public BuildingDTO entityToDTO(Building building) {
		BuildingDTO buildingDTO = mapper.map(building, BuildingDTO.class);
		List<String> typeCodes = List.of(building.getType().split(","));
		buildingDTO.setTypeCodes(typeCodes);

		String rentAreas = building.getRentArea().stream().map(RentArea::getValue).map(String::valueOf)
				.collect(Collectors.joining(","));

		buildingDTO.setRentArea(rentAreas);
		return buildingDTO;

	}

	public BuildingSearchBuilder toBuildingSearchBuilder(BuildingSearchRequest buildingSearchRequest) {

		return mapper.map(buildingSearchRequest, BuildingSearchBuilder.class);
	}
}
