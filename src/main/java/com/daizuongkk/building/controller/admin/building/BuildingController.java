package com.devon.building.controller.admin.building;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devon.building.model.dto.request.BuildingSearchRequest;

@Controller
@RequestMapping("/admin/buildings")
public class BuildingController {

	@GetMapping("/list")
	public String buildingList(@ModelAttribute BuildingSearchRequest buildingSearchRequest, Model model) {

		model.addAttribute("searchData", buildingSearchRequest);
		return "admin/building/list-buildings";
	}

	@GetMapping("/create")

	public String createBuilding() {
		return "admin/building/create-building";
	}
}
