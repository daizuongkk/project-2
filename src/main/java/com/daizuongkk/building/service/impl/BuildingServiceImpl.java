package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.AssignmentBuilding;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.District;
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

@Service
public class BuildingServiceImpl implements BuildingService {

	private final ModelMapper mapper = new ModelMapper();
	private final UserRepository userRepo;
	private final BuildingRepository buildingRepo;

	private final RentAreaRepository rentAreaRepo;

	private final AssignmentBuildingRepository assignmentBuildingRepo;

	public BuildingServiceImpl(UserRepository userRepo, BuildingRepository buildingRepo, RentAreaRepository rentAreaRepo,
			AssignmentBuildingRepository assignmentBuildingRepo) {
		this.userRepo = userRepo;
		this.buildingRepo = buildingRepo;
		this.rentAreaRepo = rentAreaRepo;
		this.assignmentBuildingRepo = assignmentBuildingRepo;
	}

	@Override
	public List<BuildingResponse> findBuildings(BuildingSearchRequest request) {

		List<Building> listBuilding = buildingRepo.findBuildings(request);

		return listBuilding.stream()
				.map(this::entityToResponse).toList();

	}

	@Override
	@Transactional
	public void create(BuildingDTO request) {

		Building newBuilding = buildingDTOtoEntity(request);
		rentAreaRepo.saveAll(newBuilding.getRentArea());
		buildingRepo.save(newBuilding);

	}

	@Override
	@Transactional
	public void deleteBuildings(List<Long> buildingIds) {

		if (buildingIds == null || buildingIds.isEmpty())
			throw new IllegalArgumentException("list id is empty");

		rentAreaRepo.deleteByBuildingIdIn(buildingIds);
		assignmentBuildingRepo.deleteByBuildingIdIn(buildingIds);
		buildingRepo.deleteByIdIn(buildingIds);
	}

	@Override
	public ResponseDTO loadStaff(Long buildingId) {

		ResponseDTO responseDTO = new ResponseDTO();
		List<User> staffs = userRepo.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);

		Set<Long> assignedBuilding = userRepo.findByAssignmentBuilding_building_id(buildingId).stream()
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
			throw new IllegalArgumentException("building id is null");

		Optional<Building> optionalBuilding = buildingRepo.findById(id);

		if (optionalBuilding.isEmpty())
			throw new ResourceNotFoundException("Not found building by id: " + id);

		Building building = optionalBuilding.get();
		BuildingDTO buildingDTO = mapper.map(building, BuildingDTO.class);

		String rentArea = building.getRentArea().stream().map(ra -> ra.getValue().toString())
				.collect(Collectors.joining(","));
		buildingDTO.setRentArea(rentArea);

		List<String> typeCodes = List.of(building.getType().split(","));
		buildingDTO.setTypeCodes(typeCodes);
		return buildingDTO;
	}

	@Override
	@Transactional
	public void assignBuilding(AssignBuildingDTO assignBuilding) {

		Long buildingId = assignBuilding.getBuildingId();

		Building building = buildingRepo.findById(buildingId)
				.orElseThrow(() -> new ResourceNotFoundException("Building not found by id: " + buildingId));

		List<Long> staffIds = assignBuilding.getStaffIds();

		// for (Long staffId : staffIds) {
		// AssignmentBuilding assignmentBuilding = new AssignmentBuilding();
		// User staff = userRepo.findById(staffId)
		// .orElseThrow(() -> new ResourceNotFoundException("Staff not found by id: " +
		// staffId));
		// assignmentBuilding.setBuilding(building);
		// assignmentBuilding.setStaff(staff);
		// assignmentBuildings.add(assignmentBuilding);
		// }

		// TODO: Dùng findByIdIn
		List<User> staffs = userRepo.findByIdIn(staffIds);

		List<AssignmentBuilding> assignmentBuildings = staffs.stream()
				.map(staff -> AssignmentBuilding.builder().building(building).staff(staff).build()).toList();

		assignmentBuildingRepo.deleteAllByBuilding_id(buildingId);
		assignmentBuildingRepo.saveAll(assignmentBuildings);
	}

	@Override
	public void updateBuilding(Long id, BuildingDTO buildingDTO) {

		if (!buildingRepo.existsById(id))
			throw new ResourceNotFoundException("Not found building to update with id: " + id);

		Building updatedBuilding = buildingDTOtoEntity(buildingDTO);
		updatedBuilding.setId(id);
		buildingRepo.save(updatedBuilding);
		rentAreaRepo.saveAll(updatedBuilding.getRentArea());

		// TODO: Sửa logic update rentarea

	}

	private BuildingResponse entityToResponse(Building building) {

		return BuildingResponse.builder().id(building.getId()).name(building.getName())
				.address(building.getStreet() + ", " + building.getWard() + ", "
						+ (building.getDistrict() != null
								? District.valueOf(building.getDistrict()).getName()
								: ""))
				.numberOfBasement(building.getNumberOfBasement()).managerName(building.getManagerName())
				.managerPhone(building.getManagerPhone()).floorArea(building.getFloorArea())
				.rentArea(
						building.getRentArea().stream().map(r -> r.getValue().toString()).collect(Collectors.joining(", ")))
				.emptyArea(null)
				.rentPrice(building.getPrice())
				.serviceFee(building.getServiceFee())
				.brokerageFee(building.getBrokerageFee()).build();
	}

	private Building buildingDTOtoEntity(BuildingDTO buildingDTO) {
		Building building = mapper.map(buildingDTO, Building.class);
		String typeCodes = String.join(",", buildingDTO.getTypeCodes());

		building.setType(typeCodes);
		List<RentArea> rentAreas = Stream.of(buildingDTO.getRentArea().split(","))
				.map(r -> RentArea.builder()
						.value(Long.parseLong(r))
						.building(building)
						.build())
				.toList();

		if (rentAreas.isEmpty())
			throw new ResourceNotFoundException("List of rentarea is empty!");

		building.setRentArea(rentAreas);

		return building;
	}
}
