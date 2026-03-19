package com.daizuongkk.building.service.impl;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;
import com.daizuongkk.building.service.BuildingService;

@Service
public class BuildingServiceImpl implements BuildingService {

	@Override
	public BuildingResponse findBuildings(BuildingSearchRequest request) {
		return null;
	}

}
