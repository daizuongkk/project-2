package com.daizuongkk.building.api.admin;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.service.BuildingService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class BuildingAPITest {

	private BuildingDTO buildingDTO;

	@MockitoBean
	private BuildingService buildingService;

	@Autowired
	private MockMvc mockMvc;

	private ResponseDTO responseDTO;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void init() {
		buildingDTO = BuildingDTO.builder()
				.name("Tòa nhà Test")
				.district("QUAN_1")
				.numberOfBasement(1L)
				.floorArea(300L)
				.rentArea("100,200")
				.price(20000000L)
				.typeCodes(List.of("NOI_THAT", "TANG_TRET"))
				.build();
	}

	@Test
	@WithMockUser(username = "admin", roles = { "MANAGER" })
	void createBuilding_shouldReturnSuccess() throws Exception {
		responseDTO = ResponseDTO.builder()
				.message("Tạo tòa nhà thành công")
				.build();

		mockMvc
				.perform(MockMvcRequestBuilders.post("/api/buildings").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(buildingDTO)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.message").value(responseDTO.getMessage()));
	}

}
