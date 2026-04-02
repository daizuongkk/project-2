package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daizuongkk.building.entity.AssignmentBuilding;

public interface AssignmentBuildingRepository extends JpaRepository<AssignmentBuilding, Long> {
	void deleteByBuildingIdIn(List<Long> buildingIds);

	List<AssignmentBuilding> findByBuilding_id(Long buildingId);

	void deleteAllByBuilding_id(Long buildingId);
}
