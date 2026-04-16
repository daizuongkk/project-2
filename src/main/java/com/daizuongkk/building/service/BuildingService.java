package com.daizuongkk.building.service;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.model.dto.AssignBuildingDTO;
import com.daizuongkk.building.model.dto.ResponseDTO;
import com.daizuongkk.building.model.dto.request.BuildingDTO;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuildingService {
	public abstract List<BuildingResponse> findBuildings(BuildingSearchRequest request);

	public abstract void create(BuildingDTO request);

	public abstract void deleteBuildings(List<Long> buildingId);

	public abstract ResponseDTO loadStaff(Long id);

	public abstract BuildingDTO findById(Long id);

	public abstract void assignBuilding(AssignBuildingDTO assignBuilding);

	public abstract Building updateBuilding(Long id, BuildingDTO request);

}
