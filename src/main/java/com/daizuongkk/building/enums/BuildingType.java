package com.daizuongkk.building.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum BuildingType {

	TANG_TRET("Tầng Trệt"),
	NOI_THAT("Nội Thất"),
	NGUYEN_CAN("Nguyên Căn");

	private final String typeName;

	BuildingType(String typeName) {
		this.typeName = typeName;
	}

	public static Map<String, String> getAlls() {
		Map<String, String> listTypes = new LinkedHashMap<>();
		for (BuildingType d : BuildingType.values()) {
			listTypes.put(d.toString(), d.typeName);
		}
		return listTypes;
	}

}
