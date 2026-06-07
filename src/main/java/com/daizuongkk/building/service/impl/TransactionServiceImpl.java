package com.daizuongkk.building.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.converter.TransactionConverter;
import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.entity.Transaction;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.model.dto.TransactionDTO;
import com.daizuongkk.building.repository.CustomerRepository;
import com.daizuongkk.building.repository.TransactionRepository;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.service.TransactionService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final TransactionConverter transactionConverter;
	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;

	@Override
	public List<TransactionDTO> getTransactions(Long customerId, String type) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng"));
		assertStaffCanAccessCustomer(customer);
		List<Transaction> transactions = transactionRepository.findByCustomer_IdAndIsActiveAndCode(customerId, true, type);
		return transactions.stream()
				.map(transactionConverter::entityToDTO)
				.toList();
	}

	@SuppressWarnings("null")
	@Override
	public TransactionDTO createTransaction(TransactionDTO request) {
		Customer customer = customerRepository.findById(request.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng"));

		User currentStaff = assertStaffCanAccessCustomer(customer);

		Transaction transaction = Transaction.builder()
				.code(request.getCode())
				.note(request.getNote())
				.customer(customer)
				.staff(currentStaff)
				.isActive(true)
				.build();

		transactionRepository.save(transaction);
		return transactionConverter.entityToDTO(transaction);
	}

	@Override
	public void updateTransaction(TransactionDTO request) {
		if (request.getId() == null) {
			throw new InvalidRequestArgumentException("Mã giao dịch không hợp lệ");
		}

		Transaction existTransaction = transactionRepository.findById(request.getId())
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch có mã: " + request.getId()));

		assertStaffCanAccessCustomer(existTransaction.getCustomer());

		existTransaction.setNote(request.getNote());
		transactionRepository.save(existTransaction);
	}

	@Override
	@Transactional
	public void deleteTransactions(List<Long> ids) {
		if (ids == null || ids.isEmpty())
			throw new InvalidRequestArgumentException("Danh sách mã giao dịch không được để trống");

		transactionRepository.deactivateTransactions(ids);
	}

	private User assertStaffCanAccessCustomer(Customer customer) {
		if (!AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			return null;
		}

		User staff = userRepository.findByUsername(AuthUtils.getCurrentUsername());
		boolean assigned = staff != null
				&& staff.getCustomers() != null
				&& staff.getCustomers().stream().anyMatch(c -> c.getId().equals(customer.getId()));

		if (!assigned) {
			throw new AccessDeniedException("Bạn không có quyền thao tác với khách hàng này");
		}

		return staff;
	}

}
