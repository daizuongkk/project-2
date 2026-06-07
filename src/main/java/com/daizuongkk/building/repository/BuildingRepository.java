package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.repository.customrepo.CustomBuildingRepository;

public interface BuildingRepository extends JpaRepository<Building, Long>, CustomBuildingRepository {
	void deleteByIdIn(List<Long> buildingIds);

	List<Building> findByIdIn(List<Long> buildingIds);

}
