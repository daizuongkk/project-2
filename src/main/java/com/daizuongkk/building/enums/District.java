package com.daizuongkk.building.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum District {

	QUAN_1("Quận 1"),
	QUAN_2("Quận 2"),
	QUAN_4("Quận 4"),
	QUAN_TB("Quận Tân Bình");

	private final String name;

	District(String name) {
		this.name = name;
	}

	public static Map<String, String> getAlls() {
		Map<String, String> listDistricts = new HashMap<>();
		for (District d : District.values()) {
			listDistricts.put(d.toString(), d.name);
		}
		return listDistricts;
	}

	public static District fromCode(String code) {
		try {
			return District.valueOf(code);
		} catch (Exception e) {
			return null;
		}
	}

}
