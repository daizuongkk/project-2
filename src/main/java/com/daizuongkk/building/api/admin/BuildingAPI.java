package com.daizuongkk.building.api.admin;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.service.BuildingService;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingAPI {

	private final BuildingService buildingService;
	private final UserService userService;

	@PostMapping()
	public ResponseEntity<ResponseDTO> addBuilding(@RequestBody @Valid BuildingDTO request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			buildErrorResponse(bindingResult, "Failed to create building");
		}

		ResponseDTO responseDTO = new ResponseDTO();
		buildingService.create(request);
		responseDTO.setMessage("Create successful buildings");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
	}

	@DeleteMapping("/{ids}")
	public ResponseEntity<String> deleteBuilding(@PathVariable List<Long> ids) {

		buildingService.deleteBuildings(ids);
		return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Successfully deleted building\"}");
	}

	@GetMapping("/{id}/staffs")
	public ResponseEntity<ResponseDTO> loadStaff(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.loadStaff(id));
	}

	@PostMapping("/assign")
	public ResponseEntity<ResponseDTO> assignBuilding(@Valid @RequestBody AssignBuildingDTO assignBuilding,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			buildErrorResponse(bindingResult, "");

		}
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setMessage("Successful assign building");
		buildingService.assignBuilding(assignBuilding);

		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	@PutMapping("")
	public ResponseEntity<ResponseDTO> updateBuilding(
			@Valid @RequestBody BuildingDTO buildingDTO, BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			User staff = userService.getUserByUsername(AuthUtils.getCurrentUser().getUsername());

			if (staff.getBuildings().stream().noneMatch(b -> b.getId().equals(buildingDTO.getId()))) {
				List<String> errorMessages = Arrays.asList("Not found");
				responseDTO.setMessage("Failed to update building");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
		}

		if (bindingResult.hasErrors()) {
			buildErrorResponse(bindingResult, "Failed to update building");
		}

		responseDTO.setMessage("Cập nhật thông tin tòa nhà thành công");
		responseDTO.setData(buildingService.updateBuilding(buildingDTO));
		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	private ResponseEntity<ResponseDTO> buildErrorResponse(BindingResult bindingResult, String message) {

		ResponseDTO responseDTO = new ResponseDTO();
		List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
		responseDTO.setMessage(message);
		responseDTO.setDetail(errorMessages);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);

	}
}
