package com.daizuongkk.building.service.impl;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.UserRole;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.model.dto.UserDTO;
import com.daizuongkk.building.model.dto.request.RegisterRequest;
import com.daizuongkk.building.model.dto.response.UserResponse;
import com.daizuongkk.building.pagination.PaginationResult;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	@PersistenceContext
	private EntityManager entityManager;

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public UserResponse register(RegisterRequest request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new EntityExistsException("Tên đăng nhập " + request.getUsername() + " đã tồn tại");
		}

		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.fullName(request.getFullName())
				.phone("")
				.userRole(UserRole.ROLE_USER.toString())
				.active(true)
				.build();

		userRepository.save(user);

		return UserResponse.builder()
				.username(user.getUsername())
				.fullName(user.getFullName())
				.email(user.getEmail())
				.role(user.getUserRole())
				.build();

	}

	@Override
	public PaginationResult<User> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {
		StringBuilder sql = new StringBuilder("SELECT NEW " + User.class.getName()
				+ "(u.id, u.username, u.active, u.userRole, u.fullName, u.phone) " + "FROM " + User.class.getName() + " u ");
		StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id) FROM " + User.class.getName() + " u ");

		if (key != null && !key.trim().isEmpty()) {
			sql.append("WHERE (LOWER(u.username) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
			countSql
					.append("WHERE (LOWER(u.username) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
		}

		sql.append("ORDER BY u.username DESC");

		TypedQuery<User> query = entityManager.createQuery(sql.toString(), User.class);
		TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

		if (key != null && !key.trim().isEmpty()) {
			String searchKey = "%" + key.toLowerCase() + "%";
			query.setParameter("key", searchKey);
			countQuery.setParameter("key", searchKey);
		}
		return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
	}

	@Override
	public void save(UserDTO userDTO) {
		String userName = userDTO.getUserName();
		User user = null;
		if (userName != null && !userName.isEmpty()) {
			user = userRepository.findByUsername(userName);
		}
		if (user != null) {
			throw new EntityExistsException("Tên đăng nhập " + userName + " đã tồn tại");
		}
		if (userDTO.getRoleCode() == null || userDTO.getRoleCode().isBlank()) {
			throw new InvalidRequestArgumentException("Vui lòng chọn vai trò");
		}
		user = new User();
		user.setUsername(userName);
		user.setActive(true);
		user.setFullName(userDTO.getFullName());
		user.setPhone(userDTO.getPhone() == null ? "" : userDTO.getPhone());
		user.setPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
		user.setUserRole(userDTO.getRoleCode());
		if (userDTO.getFileData() != null) {
			byte[] image = null;
			try {
				image = userDTO.getFileData().getBytes();
			} catch (IOException e) {
				throw new RuntimeException("Dữ liệu ảnh không hợp lệ", e);
			}
			if (image.length > 0) {
				user.setImage(image);
			}
		}
		entityManager.persist(user);
		entityManager.flush();
	}

	@Override
	public void update(UserDTO userDTO) {
		String userName = userDTO.getUserName();
		User user = userRepository.findByUsername(userName);

		if (user == null) {
			throw new EntityNotFoundException("Không tìm thấy người dùng: " + userName);
		}

		boolean isManager = AuthUtils.getAuthorities().contains("ROLE_MANAGER");
		boolean isOwner = userDTO.getUserName().equals(AuthUtils.getCurrentUsername());

		if (!isManager && !isOwner) {
			throw new AccessDeniedException("Không có quyền thực hiện yêu cầu");
		}

		user.setUsername(userName);
		user.setActive(true);
		user.setFullName(userDTO.getFullName());
		user.setPhone(userDTO.getPhone() == null ? user.getPhone() : userDTO.getPhone());

		if (isManager) {
			if (userDTO.getRoleCode() == null || userDTO.getRoleCode().isBlank()) {
				throw new InvalidRequestArgumentException("Vui lòng chọn vai trò");
			}
			user.setUserRole(userDTO.getRoleCode());
		}

		try {
			if (userDTO.getBase64Image() != null && !userDTO.getBase64Image().isEmpty()) {
				String base64String = userDTO.getBase64Image();

				if (base64String.contains(",")) {
					base64String = base64String.split(",")[1];
				}

				byte[] imageBytes = Base64.getDecoder().decode(base64String);
				user.setImage(imageBytes);
			}
		} catch (Exception e) {
			throw new RuntimeException("Dữ liệu ảnh không hợp lệ", e);
		}

		userRepository.save(user);
	}

	@SuppressWarnings("null")
	@Override
	public void delete(List<Long> ids) {
		for (Long id : ids) {
			Optional<User> user = userRepository.findById(id);
			user.ifPresent(value -> value.setActive(false));
			userRepository.flush();
		}
	}

	@Override
	public Map<Long, String> loadStaff() {
		List<User> staffs = userRepository.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);
		return staffs.stream().collect(Collectors.toMap(User::getId, User::getUsername));
	}

	@Override
	public User getUserByUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new InvalidRequestArgumentException("Tên đăng nhập không hợp lệ");
		}
		return userRepository.findByUsername(username);
	}
}
