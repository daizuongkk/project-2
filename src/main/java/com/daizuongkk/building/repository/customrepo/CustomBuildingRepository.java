package com.daizuongkk.building.repository.customrepo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;

@Repository
public interface CustomBuildingRepository {
	public abstract List<Building> findBuildings(BuildingSearchRequest request);

}
