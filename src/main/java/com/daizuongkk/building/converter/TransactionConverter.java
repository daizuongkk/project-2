package com.daizuongkk.building.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.daizuongkk.building.entity.Transaction;
import com.daizuongkk.building.model.dto.TransactionDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionConverter {
	private final ModelMapper mapper;

	public Transaction dtoToEntity(TransactionDTO dto) {

		return mapper.map(dto, Transaction.class);
	}

	public TransactionDTO entityToDTO(Transaction entity) {
		return mapper.map(entity, TransactionDTO.class);
	}
}
