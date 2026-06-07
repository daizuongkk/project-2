package com.daizuongkk.building.enums;

public enum UserRole {
	ROLE_MANAGER("Quản lý"),
	ROLE_STAFF("Nhân viên"),
	ROLE_USER("Người dùng");

	private final String label;

	UserRole(String label) {
		this.label = label;
	}

	public String getCode() {
		return this.name(); // ROLE_MANAGER
	}

	public String getLabel() {
		return label; // Manager
	}
}
