package com.daizuongkk.building.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;
import com.daizuongkk.building.pagination.PaginationResult;

@Service
public interface BuildingService {
	public abstract PaginationResult<BuildingResponse> findBuildings(BuildingSearchRequest request, int page, int size,
			int maxNavPage);

	public abstract void create(BuildingDTO request);

	public abstract void deleteBuildings(List<Long> buildingId);

	public abstract ResponseDTO loadStaff(Long id);

	public abstract BuildingDTO findById(Long id);

	public abstract void assignBuilding(AssignBuildingDTO assignBuilding);

	public abstract BuildingDTO updateBuilding(BuildingDTO request);

}
