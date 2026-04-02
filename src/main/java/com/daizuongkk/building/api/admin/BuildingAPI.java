package com.daizuongkk.building.api.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.service.BuildingService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/buildings")
public class BuildingAPI {

	@Autowired
	private BuildingService buildingService;

	@PostMapping()
	public ResponseEntity<?> addBuilding(@RequestBody @Valid BuildingDTO request, BindingResult bindingResult) {

		ResponseDTO responseDTO = new ResponseDTO();
		if (bindingResult.hasErrors()) {
			List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
			responseDTO.setDetail(errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);

		}
		buildingService.create(request);
		responseDTO.setMessage("Create building successfully");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDTO);
	}

	@DeleteMapping("/{ids}")
	public ResponseEntity<?> deleteBuilding(@PathVariable List<Long> ids) {

		buildingService.deleteBuildings(ids);
		return null;
	}

	@GetMapping("/{id}/staffs")
	public ResponseEntity<?> loadStaff(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(buildingService.loadStaff(id));
	}

	@PostMapping("/assign")
	public ResponseEntity<?> assignBuilding(@Valid @RequestBody AssignBuildingDTO assignBuilding,
			BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();
		if (bindingResult.hasErrors()) {
			List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
			responseDTO.setDetail(errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);

		}

		responseDTO.setMessage("Giao tòa nhà thành công");
		buildingService.assignBuilding(assignBuilding);

		return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
	}

	@PutMapping("path/{id}")
	public String editBuilding(@PathVariable String id, @RequestBody String entity) {

		return entity;
	}
}
