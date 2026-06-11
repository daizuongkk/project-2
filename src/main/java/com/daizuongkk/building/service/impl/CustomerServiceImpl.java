package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.converter.CustomerConverter;
import com.daizuongkk.building.entity.Customer;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.CustomerStatus;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.AssignCustomerDTO;
import com.daizuongkk.building.model.dto.CustomerDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.SearchCustomerRequest;
import com.daizuongkk.building.model.dto.response.CustomerResponse;
import com.daizuongkk.building.model.dto.response.StaffResponse;
import com.daizuongkk.building.pagination.PaginationResult;
import com.daizuongkk.building.repository.CustomerRepository;
import com.daizuongkk.building.repository.TransactionRepository;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.service.CustomerService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;
	private final UserRepository userRepo;
	private final CustomerConverter customerConverter;
	private final TransactionRepository transactionRepository;

	@Override
	public PaginationResult<CustomerResponse> findCustomers(SearchCustomerRequest request, int page, int size,
			int maxNavPage) {

		if (AuthUtils.getAuthorities().contains("ROLE_STAFF")) {

			User user = userRepo.findByUsername(AuthUtils.getCurrentUsername());
			request.setStaffId(user.getId());
		}

		PaginationResult<Customer> customers = customerRepository.searchCustomers(request, page, size, maxNavPage);

		return PaginationResult.<CustomerResponse>builder()
				.list(
						customers.getList().stream().map(customerConverter::entityToResponse).toList())
				.currentPage(customers.getCurrentPage())
				.totalPages(customers.getTotalPages())
				.maxResult(customers.getMaxResult())
				.maxNavigationPage(customers.getMaxNavigationPage())
				.totalRecords(customers.getTotalRecords())
				.navigationPages(customers.getNavigationPages())
				.build();

	}

	@Override
	public void addCustomer(CustomerDTO request) {

		if (customerRepository.existsByPhone(request.getPhone())) {
			throw new EntityExistsException("Số điện thoại " + request.getPhone() + " đã tồn tại");
		}

		List<String> authorities = AuthUtils.getAuthorities();
		boolean canChooseStatus = authorities.contains(SystemConstant.MANAGER_ROLE)
				|| authorities.contains(SystemConstant.STAFF_ROLE);
		if (!canChooseStatus || !StringUtils.hasText(request.getStatus())) {
			request.setStatus(CustomerStatus.CHUA_XU_LY.toString());
		}

		Customer customer = Customer.builder().email(request.getEmail() == null ? "" : request.getEmail())
				.fullName(request.getName())
				.phone(request.getPhone())
				.demand(request.getDemand() == null ? "" : request.getDemand())
				.status(request.getStatus())
				.createdBy(AuthUtils.getCurrentUsername())
				.isActive(true)
				.build();
		customerRepository.save(customer);
	}

	@Override
	public ResponseDTO loadStaff(Long customerId) {

		Customer customer = customerRepository.findById(
				customerId)
				.orElseThrow(() -> new EntityExistsException("Không tìm thấy khách hàng có mã: " + customerId));

		ResponseDTO responseDTO = new ResponseDTO();
		List<User> staffs = userRepo.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);

		Set<Long> assignedCustomer = customer.getStaffs().stream()
				.map(User::getId).collect(Collectors.toSet());

		List<StaffResponse> staffResponses = new ArrayList<>();
		for (User u : staffs) {
			StaffResponse staffResponse = new StaffResponse();
			staffResponse.setId(u.getId());
			staffResponse.setStaffName(u.getUsername());
			if (assignedCustomer.contains(u.getId())) {
				staffResponse.setChecked("checked");
			}
			staffResponses.add(staffResponse);
		}
		responseDTO.setData(staffResponses);
		responseDTO.setMessage("Tải danh sách nhân viên thành công");
		return responseDTO;
	}

	@Override
	@Transactional
	public void assignCustomer(AssignCustomerDTO assignCustomerDTO) {

		Customer customer = customerRepository.findById(assignCustomerDTO
				.getCustomerId())
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng có mã: " + assignCustomerDTO
						.getCustomerId()));

		List<Long> staffIds = assignCustomerDTO.getStaffIds();

		List<User> staffs = userRepo.findByIdIn(staffIds);

		if (staffs.size() != staffIds.size()) {
			throw new ResourceNotFoundException("Một số mã nhân viên không hợp lệ");
		}

		customer.setStaffs(staffs);
		customerRepository.saveAndFlush(customer);
	}

	@Override
	@Transactional
	public void deleteCustomers(List<Long> ids) {

		if (ids == null || ids.isEmpty())
			throw new InvalidRequestArgumentException("Danh sách mã khách hàng không được để trống");

		List<Long> transactionIds = transactionRepository.findAllByCustomer_IdIn(ids).stream().map(t -> t.getId()).toList();

		transactionRepository.deactivateTransactions(transactionIds);
		customerRepository.deactivateCustomers(ids);
	}

	@Override
	public CustomerDTO findById(Long id) {

		if (id == null)
			throw new InvalidRequestArgumentException("Mã khách hàng không được để trống");

		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng có mã: " + id));
		return CustomerDTO.builder()
				.id(customer.getId())
				.name(customer.getFullName())
				.phone(customer.getPhone())
				.email(customer.getEmail())
				.demand(customer.getDemand())
				.company(customer.getCompanyName())
				.status(customer.getStatus())
				.build();
	}

	@SuppressWarnings("null")
	@Override
	@Transactional
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {

		if (customerDTO.getId() == null)
			throw new InvalidRequestArgumentException("Mã khách hàng không hợp lệ");

		Customer existedCustomer = customerRepository.findById(customerDTO.getId())
				.orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng có mã: " + customerDTO.getId()));

		existedCustomer.setId(customerDTO.getId());
		existedCustomer.setFullName(customerDTO.getName());
		existedCustomer.setEmail(customerDTO.getEmail());
		existedCustomer.setPhone(customerDTO.getPhone());
		existedCustomer.setCompanyName(customerDTO.getCompany());
		existedCustomer.setDemand(customerDTO.getDemand());
		existedCustomer.setStatus(customerDTO.getStatus());
		customerRepository.saveAndFlush(existedCustomer);

		return customerConverter.entityToDTO(existedCustomer);
	}

}
