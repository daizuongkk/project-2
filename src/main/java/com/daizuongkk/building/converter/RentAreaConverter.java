package com.daizuongkk.building.converter;

import java.util.List;
import java.util.stream.Stream;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;

public class RentAreaConverter {

	public List<RentArea> toListRentArea(String rentArea, Building building) {
		return Stream.of(rentArea.split(","))
				.map(r -> RentArea.builder()
						.value(Long.parseLong(r))
						.building(building)
						.build())
				.toList();
	}

}
