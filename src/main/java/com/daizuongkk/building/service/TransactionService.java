package com.daizuongkk.building.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.model.dto.TransactionDTO;

@Service
public interface TransactionService {

	List<TransactionDTO> getTransactions(Long customerId, String type);

	TransactionDTO createTransaction(TransactionDTO request);

	void updateTransaction(TransactionDTO request);

	void deleteTransactions(List<Long> ids);

}
