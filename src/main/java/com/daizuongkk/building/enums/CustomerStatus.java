package com.daizuongkk.building.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum CustomerStatus {

	CHUA_XU_LY("Chưa xử lí"),
	DANG_XU_LY("Đang xử lí"),
	DA_XU_LY("Đã xử lí");

	private final String name;

	CustomerStatus(String name) {
		this.name = name;
	}

	public static Map<String, String> getAlls() {
		Map<String, String> customerStatus = new LinkedHashMap<>();
		for (CustomerStatus ct : CustomerStatus.values()) {
			customerStatus.put(ct.toString(), ct.getName());
		}
		return customerStatus;
	}

	public String getName() {
		return name;
	}
}
