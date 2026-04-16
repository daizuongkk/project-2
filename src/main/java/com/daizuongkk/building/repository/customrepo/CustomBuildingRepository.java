package com.daizuongkk.building.repository.customrepo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.entity.Building;

@Repository
public interface CustomBuildingRepository {
	public abstract List<Building> findBuildings(BuildingSearchBuilder request);

}
