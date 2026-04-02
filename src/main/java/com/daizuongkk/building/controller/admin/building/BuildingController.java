package com.daizuongkk.building.controller.admin.building;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daizuongkk.building.enums.BuildingType;
import com.daizuongkk.building.enums.District;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.service.BuildingService;
import com.daizuongkk.building.service.UserService;

@Controller
@RequestMapping("/admin/buildings")
public class BuildingController {

	@Autowired
	private BuildingService buildingService;
	@Autowired
	private UserService userService;

	@GetMapping("/list")
	public String buildingList(@ModelAttribute BuildingSearchRequest buildingSearchRequest, Model model) {

		model.addAttribute("searchData", buildingSearchRequest);
		model.addAttribute("listBuildings", buildingService.findBuildings(buildingSearchRequest));
		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("districts", District.getAlls());
		model.addAttribute("typeCodes", BuildingType.getAlls());
		return "admin/building/list-buildings";
	}

	@GetMapping("/create")
	public String createBuilding(Model model) {

		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("districts", District.getAlls());
		model.addAttribute("typeCodes", BuildingType.getAlls());
		model.addAttribute("building", new BuildingDTO());
		return "admin/building/create-building";
	}

	@GetMapping("/{id}/update")
	public String updateBuilding(@PathVariable Long id, Model model) {

		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("districts", District.getAlls());
		model.addAttribute("typeCodes", BuildingType.getAlls());
		model.addAttribute("building", buildingService.findById(id));
		return "admin/building/create-building";
	}

}
