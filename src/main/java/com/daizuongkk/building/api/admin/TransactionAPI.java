package com.daizuongkk.building.api.admin;

import java.util.List;

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

import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.TransactionDTO;
import com.daizuongkk.building.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionAPI {
	private final TransactionService transactionService;

	@PostMapping
	public ResponseEntity<ResponseDTO> addTransaction(@Valid @RequestBody TransactionDTO request, BindingResult result) {

		ResponseDTO responseDTO = new ResponseDTO();

		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
			responseDTO.setMessage("Thêm giao dịch thất bại");
			responseDTO.setDetail(errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		responseDTO.setData(transactionService.createTransaction(request));
		responseDTO.setMessage("Thêm giao dịch thành công");

		return ResponseEntity.ok(responseDTO);
	}

	@DeleteMapping("/{ids}")
	public ResponseEntity<String> deleteCustomers(@PathVariable List<Long> ids) {

		transactionService.deleteTransactions(ids);
		return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Xóa giao dịch thành công\"}");
	}

	@PutMapping
	public ResponseEntity<ResponseDTO> updateTransaction(@Valid @RequestBody TransactionDTO request, BindingResult result) {
		ResponseDTO responseDTO = new ResponseDTO();

		if (result.hasErrors()) {
			List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
			responseDTO.setMessage("Cập nhật giao dịch thất bại");
			responseDTO.setDetail(errorMessages);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
		}

		transactionService.updateTransaction(request);
		responseDTO.setMessage("Cập nhật giao dịch thành công");

		return ResponseEntity.ok(responseDTO);
	}

}
