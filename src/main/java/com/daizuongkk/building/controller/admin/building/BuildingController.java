package com.daizuongkk.building.controller.admin.building;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.BuildingType;
import com.daizuongkk.building.enums.District;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.service.BuildingService;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/buildings")
public class BuildingController {

	private final BuildingService buildingService;
	private final UserService userService;

	@GetMapping("/list")
	public String buildingList(
			@RequestParam(name = "page", defaultValue = "1") int page,
			@ModelAttribute BuildingSearchRequest buildingSearchRequest,
			Model model) {

		loadData(model);
		model.addAttribute("searchData", buildingSearchRequest);
		model.addAttribute("buildingResult", buildingService.findBuildings(buildingSearchRequest, page,
				SystemConstant.PAGINATION_SIZE, SystemConstant.MAX_NAV_PAGE));

		return "admin/building/list-buildings";
	}

	@GetMapping("/create")
	public String createBuilding(Model model) {

		loadData(model);
		model.addAttribute("building", new BuildingDTO());
		return "admin/building/create-building";
	}

	@GetMapping("/{id}/update")
	public String updateBuilding(@PathVariable Long id, Model model) {

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			User staff = userService.getUserByUsername(AuthUtils.getCurrentUsername());
			if (staff.getBuildings() == null || staff.getBuildings().stream().noneMatch(b -> b.getId().equals(id))) {
				return "error/404";
			}

		}
		loadData(model);

		model.addAttribute("building", buildingService.findById(id));

		return "admin/building/create-building";
	}

	private void loadData(Model model) {
		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("districts", District.getAlls());
		model.addAttribute("typeCodes", BuildingType.getAlls());
	}

}
