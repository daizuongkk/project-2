package com.daizuongkk.building.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.constant.SystemConstant;
import com.daizuongkk.building.converter.BuildingConverter;
import com.daizuongkk.building.converter.RentAreaConverter;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.entity.RentArea;
import com.daizuongkk.building.entity.User;
import com.daizuongkk.building.exception.InvalidRequestArgumentException;
import com.daizuongkk.building.exception.ResourceNotFoundException;
import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;
import com.daizuongkk.building.model.dto.response.StaffResponse;
import com.daizuongkk.building.pagination.PaginationResult;
import com.daizuongkk.building.repository.BuildingRepository;
import com.daizuongkk.building.repository.UserRepository;
import com.daizuongkk.building.service.BuildingService;
import com.daizuongkk.building.service.UserService;
import com.daizuongkk.building.utils.AuthUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

	private final UserRepository userRepo;
	private final BuildingRepository buildingRepo;
	private final BuildingConverter buildingConverter;
	private final RentAreaConverter rentAreaConverter;
	private final UserService userService;

	@Override
	public PaginationResult<BuildingResponse> findBuildings(BuildingSearchRequest request, int page, int size,
			int maxNavPage) {

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {

			User user = userRepo.findByUsername(AuthUtils.getCurrentUsername());
			request.setStaffId(user.getId());
		}
		BuildingSearchBuilder searchBuilder = buildingConverter.toBuildingSearchBuilder(request);

		PaginationResult<Building> buildings = buildingRepo.findBuildings(searchBuilder, page, size, maxNavPage);
		return PaginationResult.<BuildingResponse>builder()
				.list(
						buildings.getList().stream().map(buildingConverter::entityToResponse).toList())
				.currentPage(buildings.getCurrentPage())
				.totalPages(buildings.getTotalPages())
				.maxResult(buildings.getMaxResult())
				.maxNavigationPage(buildings.getMaxNavigationPage())
				.totalRecords(buildings.getTotalRecords())
				.navigationPages(buildings.getNavigationPages())
				.build();

	}

	@Override
	@Transactional
	public void create(BuildingDTO request) {

		Building newBuilding = buildingConverter.dtoToEntity(request);

		List<RentArea> rentAreas = rentAreaConverter.toListRentArea(request, newBuilding);

		newBuilding.setRentArea(rentAreas);

		buildingRepo.save(newBuilding);
	}

	@Override
	@Transactional
	public void deleteBuildings(List<Long> buildingIds) {

		if (buildingIds == null || buildingIds.isEmpty())
			throw new InvalidRequestArgumentException("Danh sách mã tòa nhà không được để trống");
		buildingRepo.deleteByIdIn(buildingIds);
	}

	@Override
	public ResponseDTO loadStaff(Long buildingId) {

		Building building = buildingRepo.findById(buildingId)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tòa nhà có mã: " + buildingId));

		ResponseDTO responseDTO = new ResponseDTO();
		List<User> staffs = userRepo.findByUserRoleAndActiveTrue(SystemConstant.STAFF_ROLE);

		Set<Long> assignedBuilding = building.getStaffs().stream()
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
		responseDTO.setMessage("Tải danh sách nhân viên thành công");
		return responseDTO;
	}

	@Override
	public BuildingDTO findById(Long id) {
		if (id == null)
			throw new InvalidRequestArgumentException("Mã tòa nhà không được để trống");

		Building building = buildingRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tòa nhà có mã: " + id));

		return buildingConverter.entityToDTO(building);
	}

	@SuppressWarnings("null")
	@Override
	@Transactional
	public void assignBuilding(AssignBuildingDTO assignBuilding) {

		Building building = buildingRepo.findById(assignBuilding
				.getBuildingId())
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tòa nhà có mã: " + assignBuilding
						.getBuildingId()));

		List<Long> staffIds = assignBuilding.getStaffIds();

		List<User> staffs = userRepo.findByIdIn(staffIds);

		if (staffs.size() != staffIds.size()) {
			throw new ResourceNotFoundException("Một số mã nhân viên không hợp lệ");
		}

		building.setStaffs(staffs);
		buildingRepo.saveAndFlush(building);
	}

	@SuppressWarnings("null")
	@Override
	@Transactional
	public BuildingDTO updateBuilding(BuildingDTO buildingDTO) {

		if (AuthUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
			User staff = userService.getUserByUsername(AuthUtils.getCurrentUsername());
			if (staff.getBuildings() == null
					|| staff.getBuildings().stream().noneMatch(b -> b.getId().equals(buildingDTO.getId()))) {
				throw new AccessDeniedException("Không có quyền cập nhật tòa nhà");
			}

		}

		if (buildingDTO.getId() == null)
			throw new InvalidRequestArgumentException("Mã tòa nhà không hợp lệ");

		Building exitsBuilding = buildingRepo.findById(buildingDTO.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tòa nhà có mã: " + buildingDTO.getId()));

		Building updatedBuilding = buildingConverter.dtoToEntity(buildingDTO);
		List<RentArea> rentAreas = rentAreaConverter.toListRentArea(buildingDTO, updatedBuilding);
		updatedBuilding.setRentArea(rentAreas);
		updatedBuilding.setStaffs(exitsBuilding.getStaffs());
		buildingRepo.saveAndFlush(updatedBuilding);

		return buildingConverter.entityToDTO(updatedBuilding);
	}

}
