package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.entity.AssignmentBuilding;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.enums.District;
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

	private ModelMapper mapper = new ModelMapper();
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private BuildingRepository buildingRepo;

	@Autowired
	private RentAreaRepository rentAreaRepo;

	@Autowired

	private AssignmentBuildingRepository assignmentBuildingRepo;

	@Override
	public List<BuildingResponse> findBuildings(BuildingSearchRequest request) {

		List<Building> listBuilding = buildingRepo.findBuildings(request);

		return listBuilding.stream()
				.map(building -> entityToResponse(building)).collect(Collectors.toList());

	}

	@Override
	@Transactional
	public void create(BuildingDTO request) {
		Building newBuilding = mapper.map(request, Building.class);
		String typeCodes = request.getTypeCodes().stream().collect(Collectors.joining(","));
		newBuilding.setType(typeCodes);

		List<RentArea> rentAreas = List.of(request.getRentArea().split(",")).stream()
				.map(r -> RentArea.builder().value(Long.parseLong(r)).building(newBuilding).build())
				.collect(Collectors.toList());

		if (rentAreas == null || rentAreas.isEmpty())
			throw new RuntimeException("Danh sách diện tích thuê rỗng!");

		rentAreaRepo.saveAll(rentAreas);
		newBuilding.setRentArea(rentAreas);
		buildingRepo.save(newBuilding);

	}

	@Override
	@Transactional
	public void deleteBuildings(List<Long> buildingIds) {

		if (buildingIds == null || buildingIds.isEmpty())
			throw new RuntimeException("list id is empty");

		rentAreaRepo.deleteByBuildingIdIn(buildingIds);
		assignmentBuildingRepo.deleteByBuildingIdIn(buildingIds);
		buildingRepo.deleteByIdIn(buildingIds);
	}

	@Override
	public ResponseDTO loadStaff(Long buildingId) {

		ResponseDTO responseDTO = new ResponseDTO();
		List<User> staffs = userRepo.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);

		Set<Long> assignedBuilding = userRepo.findByAssignmentBuilding_building_id(buildingId).stream()
				.map(u -> u.getId()).collect(Collectors.toSet());

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
			throw new RuntimeException("building id is null");

		Optional<Building> optionalBuilding = buildingRepo.findById(id);

		if (!optionalBuilding.isPresent())
			throw new RuntimeException("Not found building by id: " + id);

		Building building = optionalBuilding.get();
		BuildingDTO buildingDTO = mapper.map(building, BuildingDTO.class);

		String rentArea = building.getRentArea().stream().map(ra -> ra.getValue().toString())
				.collect(Collectors.joining(", "));
		buildingDTO.setRentArea(rentArea);

		List<String> typeCodes = List.of(building.getType().split(","));
		buildingDTO.setTypeCodes(typeCodes);
		return buildingDTO;
	}

	private BuildingResponse entityToResponse(Building building) {

		return BuildingResponse.builder().id(building.getId()).name(building.getName())
				.address(building.getStreet() + ", " + building.getWard() + ", "
						+ (District.fromCode(building.getDistrict()) != null
								? District.fromCode(building.getDistrict()).getName()
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

	@Override
	@Transactional
	public void assignBuilding(AssignBuildingDTO assignBuilding) {

		Long buildingId = assignBuilding.getBuildingId();

		Building building = buildingRepo.findById(buildingId)
				.orElseThrow(() -> new RuntimeException("Building not found by id: " + buildingId));

		List<Long> staffIds = assignBuilding.getStaffIds();

		List<AssignmentBuilding> assignmentBuildings = new ArrayList<>();

		for (Long staffId : staffIds) {
			AssignmentBuilding assignmentBuilding = new AssignmentBuilding();
			User staff = userRepo.findById(staffId)
					.orElseThrow(() -> new RuntimeException("Staff not found by id: " + staffId));
			assignmentBuilding.setBuilding(building);
			assignmentBuilding.setStaff(staff);
			assignmentBuildings.add(assignmentBuilding);
		}

		assignmentBuildingRepo.deleteAllByBuilding_id(buildingId);

		assignmentBuildingRepo.saveAll(assignmentBuildings);
	}
}
