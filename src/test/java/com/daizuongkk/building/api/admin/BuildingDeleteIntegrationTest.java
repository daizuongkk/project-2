package com.daizuongkk.building.api.admin;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.repository.BuildingRepository;
import com.daizuongkk.building.repository.RentAreaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BuildingDeleteIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BuildingRepository buildingRepository;

	@Autowired
	private RentAreaRepository rentAreaRepository;

	@Test
	@DisplayName("Should successfully delete multiple buildings and cascade delete rent areas when user has MANAGER role")
	@WithMockUser(username = "admin", roles = { "MANAGER" })
	void testDeleteBuildingsSuccess_AsManager() throws Exception {
		// 1. Arrange: Seed test buildings and rent areas
		Building building1 = Building.builder()
				.name("Senior Tower A")
				.street("123 Nguyen Trai")
				.ward("Ward 1")
				.district("QUAN_1")
				.price(30L)
				.type("TANG_TRET")
				.rentArea(new ArrayList<>())
				.build();

		RentArea area1 = RentArea.builder().value(150L).building(building1).build();
		RentArea area2 = RentArea.builder().value(250L).building(building1).build();
		building1.getRentArea().add(area1);
		building1.getRentArea().add(area2);

		Building building2 = Building.builder()
				.name("Senior Tower B")
				.street("456 Nguyen Trai")
				.ward("Ward 1")
				.district("QUAN_1")
				.price(45L)
				.type("NGUYEN_CAN")
				.rentArea(new ArrayList<>())
				.build();

		RentArea area3 = RentArea.builder().value(500L).building(building2).build();
		building2.getRentArea().add(area3);

		Building savedBuilding1 = buildingRepository.save(building1);
		Building savedBuilding2 = buildingRepository.save(building2);

		Long id1 = savedBuilding1.getId();
		Long id2 = savedBuilding2.getId();

		assertThat(buildingRepository.existsById(id1)).isTrue();
		assertThat(buildingRepository.existsById(id2)).isTrue();
		assertThat(rentAreaRepository.findAllByBuilding_id(id1)).hasSize(2);
		assertThat(rentAreaRepository.findAllByBuilding_id(id2)).hasSize(1);

		mockMvc.perform(delete("/api/buildings/{ids}", id1 + "," + id2))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Xóa tòa nhà thành công")));

		assertThat(buildingRepository.existsById(id1)).isFalse();
		assertThat(buildingRepository.existsById(id2)).isFalse();
		assertThat(rentAreaRepository.findAllByBuilding_id(id1)).isEmpty();
		assertThat(rentAreaRepository.findAllByBuilding_id(id2)).isEmpty();
	}

	@Test
	@DisplayName("Should forbid building deletion when authenticated user is STAFF")
	@WithMockUser(username = "staff1", roles = { "STAFF" })
	void testDeleteBuildings_AsStaff_Forbidden() throws Exception {
		Building testBuilding = buildingRepository.save(Building.builder()
				.name("Staff Target Tower")
				.street("789 Le Lai")
				.ward("Ward 3")
				.district("QUAN_3")
				.price(25L)
				.type("NOI_THAT")
				.build());
		Long id = testBuilding.getId();

		mockMvc.perform(delete("/api/buildings/{ids}", id))
				.andExpect(status().isForbidden());

		assertThat(buildingRepository.existsById(id)).isTrue();
	}

	@Test
	@DisplayName("Should redirect or deny access when user is unauthenticated")
	void testDeleteBuildings_Unauthenticated_RedirectOrUnauthorized() throws Exception {
		Building testBuilding = buildingRepository.save(Building.builder()
				.name("Anonymous Target Tower")
				.street("321 Ly Tu Trong")
				.ward("Ward 5")
				.district("QUAN_1")
				.price(50L)
				.type("TANG_TRET")
				.build());
		Long id = testBuilding.getId();

		mockMvc.perform(delete("/api/buildings/{ids}", id))
				.andExpect(status().is3xxRedirection());

		assertThat(buildingRepository.existsById(id)).isTrue();
	}

	@Test
	@DisplayName("Should return 500 Internal Server Error when path variable cannot be parsed")
	@WithMockUser(username = "admin", roles = { "MANAGER" })
	void testDeleteBuildings_InvalidPathVariable_BadRequest() throws Exception {
		mockMvc.perform(delete("/api/buildings/{ids}", "abc"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("Failed to convert value of type")));
	}

	@Test
	@DisplayName("Should return 500 Internal Server Error when deleting a building referenced in order details")
	@WithMockUser(username = "admin", roles = { "MANAGER" })
	@org.springframework.transaction.annotation.Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
	void testDeleteBuildings_ReferencedInOrderDetails_ServerError() throws Exception {
		Long referencedBuildingId = 1L;

		assertThat(buildingRepository.existsById(referencedBuildingId)).isTrue();

		mockMvc.perform(delete("/api/buildings/{ids}", referencedBuildingId))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("message")));

		assertThat(buildingRepository.existsById(referencedBuildingId)).isTrue();
	}
}
