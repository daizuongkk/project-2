package com.daizuongkk.building.converter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.enums.District;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuildingConverter {

	private final ModelMapper mapper;

	public BuildingResponse entityToResponse(Building building) {

		return BuildingResponse.builder().id(building.getId()).name(building.getName())
				.address(building.getStreet() + ", " + building.getWard() + ", "
						+ (building.getDistrict() != null
								? District.valueOf(building.getDistrict()).getName()
								: ""))
				.numberOfBasement(building.getNumberOfBasement()).managerName(building.getManagerName())
				.managerPhone(building.getManagerPhone()).floorArea(building.getFloorArea())
				.rentArea(
						building.getRentArea().stream().map(r -> r.getValue().toString()).collect(Collectors.joining(", ")))
				.emptyArea(null)
				.rentPrice(building.getPrice())
				.serviceFee(building.getServiceFee())
				.brokerageFee(building.getBrokerageFee()).build();
	}

	public Building dtoToEntity(BuildingDTO buildingDTO) {
		Building building = mapper.map(buildingDTO, Building.class);
		String typeCodes = String.join(",", buildingDTO.getTypeCodes());

		building.setType(typeCodes);
		List<RentArea> rentAreas = Stream.of(buildingDTO.getRentArea().split(","))
				.map(r -> RentArea.builder()
						.value(Long.parseLong(r))
						.building(building)
						.build())
				.toList();

		if (rentAreas.isEmpty())
			throw new ResourceNotFoundException("List of rentarea is empty!");

		building.setRentArea(rentAreas);

		return building;
	}

	public BuildingSearchBuilder toBuildingSearchBuilder(BuildingSearchRequest buildingSearchRequest) {
		return mapper.map(buildingSearchRequest, BuildingSearchBuilder.class);
	}
}
