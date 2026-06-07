package com.daizuongkk.building.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum TransactionType {
	DDX("Dẫn đi xem"), CSKH("Chăm sóc khách hàng");

	private String name;

	TransactionType(String name) {
		this.name = name;
	}

	public static Map<String, String> getAlls() {
		Map<String, String> list = new LinkedHashMap<>();
		for (TransactionType type : TransactionType.values()) {
			list.put(type.toString(), type.name);
		}
		return list;
	}

	public static District fromCode(String code) {
		try {
			return District.valueOf(code);
		} catch (Exception e) {
			return null;
		}
	}
}
