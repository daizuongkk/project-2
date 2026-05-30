package com.daizuongkk.building.repository.customrepo;

import org.springframework.stereotype.Repository;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.pagination.PaginationResult;

@Repository
public interface CustomBuildingRepository {
	public abstract PaginationResult<Building> findBuildings(BuildingSearchBuilder request, int page, int size,
			int maxNavPage);

}
