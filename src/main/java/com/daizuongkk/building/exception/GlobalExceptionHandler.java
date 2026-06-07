package com.daizuongkk.building.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daizuongkk.building.model.dto.ResponseDTO;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UnauthorizedException.class)

	public ResponseEntity<ResponseDTO> handleUnauthorized(UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ResponseDTO> handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(InvalidRequestArgumentException.class)
	public ResponseEntity<ResponseDTO> handleInvalidRequestArgument(InvalidRequestArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseDTO> handleAccessDenied(AccessDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ResponseDTO> handleEntityNotFound(EntityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<ResponseDTO> handleEntityExists(EntityExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseDTO> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseDTO.builder().message(ex.getMessage() == null ? "Đã xảy ra lỗi hệ thống" : ex.getMessage()).build());
	}

}
