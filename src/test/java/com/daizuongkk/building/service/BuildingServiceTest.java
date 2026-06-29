package com.daizuongkk.building.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.repository.BuildingRepository;
import com.daizuongkk.building.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
class BuildingServiceTest {

	// =========================
	// TESTCONTAINERS
	// =========================
	@Container
	@ServiceConnection
	static final MySQLContainer<?> mySqlContainer = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("estateadvance")
			.withUsername("root")
			.withPassword("daizuongkk");

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private BuildingService buildingService;

	// =========================
	// SHARED DATA (NO SAVE HERE)
	// =========================
	private User user1;
	private User user2;

	private Building building1;
	private Building building2;

	private AssignBuildingDTO dto;

	// =========================
	// SECURITY
	// =========================
	@BeforeEach
	void setupSecurity() {
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(
				"manager",
				"123",
				authorities);

		Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// =========================
	// INIT DATA (NO SAVE)
	// =========================
	@BeforeEach
	void initData() {

		user1 = new User();
		user1.setUsername("u1");
		user1.setPassword("123");
		user1.setActive(true);
		user1.setFullName("User 1");
		user1.setEmail("u1@test.com");
		user1.setPhone("0901");
		user1.setUserRole(SystemConstant.STAFF_ROLE);

		user2 = new User();
		user2.setUsername("u2");
		user2.setPassword("123");
		user2.setActive(true);
		user2.setFullName("User 2");
		user2.setEmail("u2@test.com");
		user2.setPhone("0902");
		user2.setUserRole(SystemConstant.STAFF_ROLE);

		building1 = new Building();
		building1.setName("Tower A");
		building1.setStreet("123");
		building1.setWard("Ward");
		building1.setDistrict("QUAN_1");
		building1.setPrice(100L);
		building1.setType("NOI_THAT");
		building1.setRentArea(new ArrayList<>());

		building2 = new Building();
		building2.setName("Tower B");
		building2.setStreet("456");
		building2.setWard("Ward");
		building2.setDistrict("QUAN_1");
		building2.setPrice(200L);
		building2.setType("NGUYEN_CAN");
		building2.setRentArea(new ArrayList<>());

		dto = new AssignBuildingDTO();
	}

	// =========================
	// DELETE TESTS
	// =========================

	@Test
	void deleteBuildings_success() {

		building1 = buildingRepository.save(building1);
		building2 = buildingRepository.save(building2);

		// PRE CHECK
		assertThat(buildingRepository.existsById(building1.getId())).isTrue();
		assertThat(buildingRepository.existsById(building2.getId())).isTrue();

		buildingService.deleteBuildings(List.of(building1.getId(), building2.getId()));

		// POST CHECK
		assertThat(buildingRepository.existsById(building1.getId())).isFalse();
		assertThat(buildingRepository.existsById(building2.getId())).isFalse();
	}

	@Test
	void deleteBuildings_null() {
		assertThrows(InvalidRequestArgumentException.class,
				() -> buildingService.deleteBuildings(null));
	}

	@Test
	void deleteBuildings_empty() {
		assertThrows(InvalidRequestArgumentException.class,
				() -> buildingService.deleteBuildings(List.of()));
	}

	@Test
	void deleteBuildings_notExists() {
		assertThatCode(() -> buildingService.deleteBuildings(List.of(999L, 888L))).doesNotThrowAnyException();
	}

	@Test
	@Transactional
	void assignBuilding_success() {

		user1 = userRepo.save(user1);
		user2 = userRepo.save(user2);
		building1 = buildingRepository.save(building1);

		Building before = buildingRepository.findById(building1.getId()).orElseThrow();
		assertThat(before.getStaffs()).isNullOrEmpty();

		dto.setBuildingId(building1.getId());
		dto.setStaffIds(List.of(user1.getId(), user2.getId()));

		buildingService.assignBuilding(dto);

		Building after = buildingRepository.findById(building1.getId()).orElseThrow();

		assertThat(after.getStaffs()).isNotNull();
		assertThat(after.getStaffs()).hasSize(2);
	}

	@Test
	void assignBuilding_buildingNotFound() {

		dto.setBuildingId(999999L);
		dto.setStaffIds(List.of(1L, 2L));

		assertThrows(ResourceNotFoundException.class,
				() -> buildingService.assignBuilding(dto));
	}

	@Test
	void assignBuilding_invalidStaff() {

		user1 = userRepo.save(user1);
		building1 = buildingRepository.save(building1);

		dto.setBuildingId(building1.getId());
		dto.setStaffIds(List.of(user1.getId(), 999999L));

		assertThrows(ResourceNotFoundException.class,
				() -> buildingService.assignBuilding(dto));
	}

	@Test
	void assignBuilding_emptyStaff() {

		building1 = buildingRepository.save(building1);

		dto.setBuildingId(building1.getId());
		dto.setStaffIds(List.of());

		assertThrows(Exception.class,
				() -> buildingService.assignBuilding(dto));
	}

	@Test
	void assignBuilding_nullStaff() {

		building1 = buildingRepository.save(building1);

		dto.setBuildingId(building1.getId());
		dto.setStaffIds(null);

		assertThrows(Exception.class,
				() -> buildingService.assignBuilding(dto));
	}
}