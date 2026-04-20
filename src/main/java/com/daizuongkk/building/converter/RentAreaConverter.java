package com.daizuongkk.building.converter;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.model.dto.request.BuildingDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RentAreaConverter {

	public List<RentArea> toListRentArea(BuildingDTO buildingDTO, Building building) {

		return Stream.of(buildingDTO.getRentArea().split(","))
				.map(r -> RentArea.builder()
						.value(Long.parseLong(r))
						.building(building)
						.build())
				.toList();
	}

}
