package com.daizuongkk.building.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.model.dto.UserDTO;
import com.daizuongkk.building.model.dto.request.RegisterRequest;
import com.daizuongkk.building.model.dto.response.UserResponse;
import com.daizuongkk.building.pagination.PaginationResult;

@Service
public interface UserService {
	PaginationResult<User> listUserInfo(String key, int page, int maxResult, int maxNavigationPage);

	void save(UserDTO userDTO);

	void update(UserDTO userDTO);

	void delete(List<Long> ids);

	Map<Long, String> loadStaff();

	User getUserByUsername(String username);

	UserResponse register(RegisterRequest request);
}
