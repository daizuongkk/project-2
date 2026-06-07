package com.daizuongkk.building.api.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
import com.daizuongkk.building.model.dto.AssignCustomerDTO;
import com.daizuongkk.building.model.dto.CustomerDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.service.CustomerService;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerAPI {

	private final CustomerService customerService;
	private final UserService userService;

	@PostMapping
	public ResponseEntity<ResponseDTO> addCustomer(
			@Valid @RequestBody CustomerDTO request,
			Model model,
			BindingResult result) {

		ResponseDTO responseDTO = new ResponseDTO();

		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors()
					.stream()
					.map(FieldError::getDefaultMessage)
					.toList();

			responseDTO.setMessage("Dữ liệu khách hàng không hợp lệ");
			responseDTO.setDetail(errorMessages);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		customerService.addCustomer(request);

		responseDTO.setMessage("Thêm khách hàng thành công");

		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/{id}/staffs")
	public ResponseEntity<ResponseDTO> loadStaff(@PathVariable Long id) {
		return ResponseEntity.ok(customerService.loadStaff(id));
	}

	@PostMapping("/assign")
	public ResponseEntity<ResponseDTO> assignBuilding(
			@Valid @RequestBody AssignCustomerDTO assignCustomerDTO,
			BindingResult result) {

		ResponseDTO responseDTO = new ResponseDTO();

		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors()
					.stream()
					.map(FieldError::getDefaultMessage)
					.toList();

			responseDTO.setMessage("Phân công khách hàng thất bại");
			responseDTO.setDetail(errorMessages);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		customerService.assignCustomer(assignCustomerDTO);

		responseDTO.setMessage("Phân công khách hàng thành công");

		return ResponseEntity.ok(responseDTO);
	}

	@DeleteMapping("/{ids}")
	public ResponseEntity<String> deleteCustomers(@PathVariable List<Long> ids) {

		customerService.deleteCustomers(ids);

		return ResponseEntity.ok(
				"{\"message\":\"Xóa khách hàng thành công\"}");
	}

	@PutMapping
	public ResponseEntity<ResponseDTO> updateCustomer(
			@Valid @RequestBody CustomerDTO customerDTO,
			BindingResult result) {

		ResponseDTO responseDTO = new ResponseDTO();

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {

			User staff = userService.getUserByUsername(
					AuthUtils.getCurrentUsername());

			if (staff.getCustomers()
					.stream()
					.noneMatch(c -> c.getId().equals(customerDTO.getId()))) {

				responseDTO.setMessage("Không tìm thấy khách hàng");
				responseDTO.setDetail(List.of(
						"Bạn không có quyền cập nhật khách hàng này"));

				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(responseDTO);
			}
		}

		if (result.hasErrors()) {

			List<String> errorMessages = result.getFieldErrors()
					.stream()
					.map(FieldError::getDefaultMessage)
					.toList();

			responseDTO.setMessage("Dữ liệu khách hàng không hợp lệ");
			responseDTO.setDetail(errorMessages);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(responseDTO);
		}

		responseDTO.setMessage("Cập nhật thông tin khách hàng thành công");
		responseDTO.setData(customerService.updateCustomer(customerDTO));

		return ResponseEntity.ok(responseDTO);
	}
}
