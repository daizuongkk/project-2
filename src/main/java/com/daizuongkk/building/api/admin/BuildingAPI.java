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
			return buildErrorResponse(bindingResult, "Tạo tòa nhà thất bại");
		}

		ResponseDTO responseDTO = new ResponseDTO();
		buildingService.create(request);
		responseDTO.setMessage("Tạo tòa nhà thành công");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
	}

	@DeleteMapping("/{ids}")
	public ResponseEntity<String> deleteBuilding(@PathVariable List<Long> ids) {

		buildingService.deleteBuildings(ids);
		return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Xóa tòa nhà thành công\"}");
	}

	@GetMapping("/{id}/staffs")
	public ResponseEntity<ResponseDTO> loadStaff(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.loadStaff(id));
	}

	@PostMapping("/assign")
	public ResponseEntity<ResponseDTO> assignBuilding(@Valid @RequestBody AssignBuildingDTO assignBuilding,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return buildErrorResponse(bindingResult, "Giao tòa nhà cho nhân viên thất bại");

		}
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setMessage("Giao tòa nhà cho nhân viên thành công");
		buildingService.assignBuilding(assignBuilding);

		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	@PutMapping
	public ResponseEntity<ResponseDTO> updateBuilding(
			@Valid @RequestBody BuildingDTO buildingDTO, BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			User staff = userService.getUserByUsername(AuthUtils.getCurrentUsername());

			if (staff.getBuildings() == null
					|| staff.getBuildings().stream().noneMatch(b -> b.getId().equals(buildingDTO.getId()))) {
				List<String> errorMessages = Arrays.asList("Không tìm thấy tòa nhà được giao cho nhân viên này");
				responseDTO.setMessage("Cập nhật tòa nhà thất bại");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDTO);
			}
		}

		if (bindingResult.hasErrors()) {
			return buildErrorResponse(bindingResult, "Cập nhật tòa nhà thất bại");
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
