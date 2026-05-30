package com.daizuongkk.building.api.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daizuongkk.building.model.dto.PasswordDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.UserDTO;
import com.daizuongkk.building.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserAPI {

	private final UserService userService;

	public UserAPI(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<?> createUser(@Valid @ModelAttribute UserDTO user, BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (bindingResult.hasErrors()) {
				List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
						.collect(Collectors.toList());

				responseDTO.setMessage("Validation failed");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.badRequest().body(responseDTO);
			}
			userService.save(user);
			responseDTO.setMessage("Create Successfully");
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			responseDTO.setMessage(e.getMessage());
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateUser(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (bindingResult.hasErrors()) {
				List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
						.collect(Collectors.toList());

				responseDTO.setMessage("Validation failed");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.badRequest().body(responseDTO);
			}
			userService.update(userDTO);
			responseDTO.setMessage("Update Successfully");
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			responseDTO.setMessage(e.getMessage());
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@DeleteMapping
	public ResponseEntity<?> deleteUsers(@RequestBody List<Long> idList) {
		if (!idList.isEmpty()) {
			userService.delete(idList);
		}
		return ResponseEntity.ok().body("{ \"message\": \"Delete Successfully\" }");
	}

	@PutMapping("/password/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		return ResponseEntity.ok().body(responseDTO);
	}
}
