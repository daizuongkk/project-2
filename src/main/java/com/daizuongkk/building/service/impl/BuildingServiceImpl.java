package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.converter.BuildingConverter;
import com.daizuongkk.building.entity.AssignmentBuilding;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;
import com.daizuongkk.building.model.dto.response.StaffResponse;
import com.daizuongkk.building.repository.AssignmentBuildingRepository;
import com.daizuongkk.building.repository.BuildingRepository;
import com.daizuongkk.building.repository.RentAreaRepository;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.service.BuildingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

	private final UserRepository userRepo;
	private final BuildingRepository buildingRepo;

	private final RentAreaRepository rentAreaRepo;

	private final AssignmentBuildingRepository assignmentBuildingRepo;
	private final BuildingConverter buildingConverter;

	@Override
	public List<BuildingResponse> findBuildings(BuildingSearchRequest request) {

		BuildingSearchBuilder searchBuilder = buildingConverter.toBuildingSearchBuilder(request);

		List<Building> listBuilding = buildingRepo.findBuildings(searchBuilder);

		return listBuilding.stream()
				.map(buildingConverter::entityToResponse).toList();
	}

	@Override
	@Transactional
	public void create(BuildingDTO request) {

		Building newBuilding = buildingConverter.dtoToEntity(request);

		buildingRepo.save(newBuilding);
		rentAreaRepo.saveAll(newBuilding.getRentArea());

	}

	@Override
	@Transactional
	public void deleteBuildings(List<Long> buildingIds) {

		if (buildingIds == null || buildingIds.isEmpty())
			throw new InvalidRequestArgumentException("list id is empty");

		rentAreaRepo.deleteByBuildingIdIn(buildingIds);
		assignmentBuildingRepo.deleteByBuildingIdIn(buildingIds);
		buildingRepo.deleteByIdIn(buildingIds);
	}

	@Override
	public ResponseDTO loadStaff(Long buildingId) {

		Building building = buildingRepo.findById(buildingId)
				.orElseThrow(() -> new ResourceNotFoundException("Not found building with id: " + buildingId));

		ResponseDTO responseDTO = new ResponseDTO();
		List<User> staffs = userRepo.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);

		Set<Long> assignedBuilding = userRepo.findByAssignmentBuilding_Building(building).stream()
				.map(User::getId).collect(Collectors.toSet());

		List<StaffResponse> staffResponses = new ArrayList<>();
		for (User u : staffs) {
			StaffResponse staffResponse = new StaffResponse();
			staffResponse.setId(u.getId());
			staffResponse.setStaffName(u.getFullName());
			if (assignedBuilding.contains(u.getId())) {
				staffResponse.setChecked("checked");
			}
			staffResponses.add(staffResponse);
		}
		responseDTO.setData(staffResponses);
		responseDTO.setMessage("Load staff successfully");
		return responseDTO;
	}

	@Override
	public BuildingDTO findById(Long id) {
		if (id == null)
			throw new InvalidRequestArgumentException("building id is null");

		Building building = buildingRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Not found building by id: " + id));

		return buildingConverter.entityToDTO(building);
	}

	@Override
	@Transactional
	public void assignBuilding(AssignBuildingDTO assignBuilding) {
		Long buildingId = assignBuilding.getBuildingId();

		Building building = buildingRepo.findById(buildingId)
				.orElseThrow(() -> new ResourceNotFoundException("Building not found by id: " + buildingId));

		List<Long> staffIds = assignBuilding.getStaffIds();

		if (staffIds == null || staffIds.isEmpty()) {
			assignmentBuildingRepo.deleteAllByBuilding_id(buildingId);
			return;
		}

		List<User> staffs = userRepo.findByIdIn(staffIds);

		if (staffs.size() != staffIds.size()) {
			throw new ResourceNotFoundException("Some staff IDs are invalid");
		}

		List<AssignmentBuilding> assignmentBuildings = staffs.stream()
				.map(staff -> AssignmentBuilding.builder().building(building).staff(staff).build()).toList();

		assignmentBuildingRepo.deleteAllByBuilding_id(buildingId);
		assignmentBuildingRepo.saveAll(assignmentBuildings);
	}

	@Override
	@Transactional
	public BuildingDTO updateBuilding(BuildingDTO buildingDTO) {

		if (!buildingRepo.existsById(buildingDTO.getId()))
			throw new ResourceNotFoundException("Not found building to update with id: " + buildingDTO.getId());

		Building updatedBuilding = buildingConverter.dtoToEntity(buildingDTO);
		buildingRepo.saveAndFlush(updatedBuilding);
		rentAreaRepo.deleteAllByBuilding_id(buildingDTO.getId());

		rentAreaRepo.saveAll(updatedBuilding.getRentArea());
		return buildingConverter.entityToDTO(updatedBuilding);
	}

}
