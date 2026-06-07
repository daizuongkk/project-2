package com.daizuongkk.building.api.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daizuongkk.building.model.dto.PasswordDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.UserDTO;
import com.daizuongkk.building.model.dto.request.RegisterRequest;
import com.daizuongkk.building.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAPI {

	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterRequest request, BindingResult result) {

		ResponseDTO responseDTO = new ResponseDTO();

		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
			responseDTO.setMessage("Đăng ký thất bại");
			responseDTO.setDetail(errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		if (!request.getPassword().equals(request.getConfirmPassword())) {
			responseDTO.setMessage("Mật khẩu xác nhận không khớp");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		responseDTO.setData(userService.register(request));
		responseDTO.setMessage("Đăng ký thành công");

		return ResponseEntity.ok(responseDTO);
	}

	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO user, BindingResult bindingResult) {
		ResponseDTO responseDTO = new ResponseDTO();
		try {
			if (bindingResult.hasErrors()) {
				List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
						.collect(Collectors.toList());

				responseDTO.setMessage("Dữ liệu tài khoản không hợp lệ");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.badRequest().body(responseDTO);
			}
			userService.save(user);
			responseDTO.setMessage("Tạo tài khoản thành công");
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

				responseDTO.setMessage("Dữ liệu tài khoản không hợp lệ");
				responseDTO.setDetail(errorMessages);
				return ResponseEntity.badRequest().body(responseDTO);
			}
			userService.update(userDTO);
			responseDTO.setMessage("Cập nhật tài khoản thành công");
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
		return ResponseEntity.ok().body("{ \"message\": \"Xóa tài khoản thành công\" }");
	}

	@PutMapping("/password/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody PasswordDTO passwordDTO) {
		ResponseDTO responseDTO = new ResponseDTO();
		responseDTO.setMessage("Chức năng đổi mật khẩu chưa được hỗ trợ");
		return ResponseEntity.ok().body(responseDTO);
	}
}
