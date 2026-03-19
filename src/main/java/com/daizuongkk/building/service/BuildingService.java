package com.daizuongkk.building.service;

import org.springframework.stereotype.Service;

import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.model.dto.response.BuildingResponse;

@Service
public interface BuildingService {
	public abstract BuildingResponse findBuildings(BuildingSearchRequest request);
}
