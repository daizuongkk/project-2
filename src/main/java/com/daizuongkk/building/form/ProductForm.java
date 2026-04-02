package com.daizuongkk.building.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import com.daizuongkk.building.entity.Building;

@Getter
@Setter
public class ProductForm {
	private Long id;
	private String name;
	private Long price;

	private boolean newProduct = false;

	// Upload file.
	private MultipartFile fileData;

	public ProductForm() {
		this.newProduct = true;
	}

	public ProductForm(Building building) {
		this.id = building.getId();
		this.name = building.getName();
		this.price = building.getPrice();
	}

}
