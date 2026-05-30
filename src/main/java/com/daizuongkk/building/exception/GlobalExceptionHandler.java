package com.daizuongkk.building.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daizuongkk.building.model.dto.ResponseDTO;

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
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseDTO> handleRuntimeException(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ResponseDTO.builder().message(ex.getMessage()).build());
	}

}
