package com.daizuongkk.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.daizuongkk.building.entity.RentArea;

public interface RentAreaRepository extends JpaRepository<RentArea, Long> {
	void deleteByBuildingIdIn(List<Long> buildingIds);

	List<RentArea> findAllByBuilding_id(Long id);
}
