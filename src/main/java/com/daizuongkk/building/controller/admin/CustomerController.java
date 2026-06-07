package com.daizuongkk.building.controller.admin;

import com.daizuongkk.building.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.CustomerStatus;
import com.daizuongkk.building.enums.TransactionType;
import com.daizuongkk.building.model.dto.CustomerDTO;
import com.daizuongkk.building.model.dto.request.SearchCustomerRequest;
import com.daizuongkk.building.service.CustomerService;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final TransactionService transactionService;
	private final CustomerService customerService;
	private final UserService userService;

	@GetMapping("/list")
	public String findCustomer(@RequestParam(name = "page", defaultValue = "1") int page,
			@ModelAttribute SearchCustomerRequest request, Model model) {
		model.addAttribute("searchData", request);

		model.addAttribute("status", CustomerStatus.getAlls());
		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("customers", customerService.findCustomers(request, page, SystemConstant.PAGINATION_SIZE,
				SystemConstant.MAX_NAV_PAGE));

		return "list-customers";
	}

	@GetMapping("/create")
	public String createCustomer(Model model) {

		model.addAttribute("status", CustomerStatus.getAlls());
		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("customer", new CustomerDTO());
		return "edit-customer";
	}

	@GetMapping("/{id}/update")
	public String updateCustomer(@PathVariable Long id, Model model) {

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			User staff = userService.getUserByUsername(AuthUtils.getCurrentUsername());
			if (staff.getCustomers() == null || staff.getCustomers().stream().noneMatch(ctm -> ctm.getId().equals(id))) {
				return "error/404";
			}

		}

		model.addAttribute("transactionType", TransactionType.getAlls());
		model.addAttribute("status", CustomerStatus.getAlls());
		model.addAttribute("staffs", userService.loadStaff());
		model.addAttribute("CSKH", transactionService.getTransactions(id, "CSKH"));
		model.addAttribute("DDX", transactionService.getTransactions(id, "DDX"));
		model.addAttribute("customer", customerService.findById(id));

		return "edit-customer";
	}
}
