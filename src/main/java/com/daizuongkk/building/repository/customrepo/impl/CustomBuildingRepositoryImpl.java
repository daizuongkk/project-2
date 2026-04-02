package com.daizuongkk.building.repository.customrepo.impl;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.model.dto.request.BuildingSearchRequest;
import com.daizuongkk.building.repository.customrepo.CustomBuildingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomBuildingRepositoryImpl implements CustomBuildingRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public List<Building> findBuildings(BuildingSearchRequest request) {
		StringBuilder queryStr = new StringBuilder("SELECT DISTINCT b.* FROM building b ");
		buildJoinClause(request, queryStr);

		Query query = null;
		try {
			buildQueryClause(request, queryStr);
			query = entityManager.createNativeQuery(queryStr.toString(), Building.class);
			setQueryValue(query, request);

		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return query.getResultList();
	}

	private String buildJoinClause(BuildingSearchRequest request, StringBuilder query) {

		if (request.getMaxRentArea() != null || request.getMinRentArea() != null)
			query.append("JOIN rentarea ra ON ra.buildingid = b.id ");

		if (request.getStaffId() != null)
			query.append("JOIN assignmentbuilding ab ON ab.buildingid = b.id ");

		return query.toString();
	}

	private String buildQueryClause(BuildingSearchRequest request, StringBuilder query)
			throws IllegalArgumentException, IllegalAccessException {

		query.append(" WHERE 1=1 ");
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object value = field.get(request);
			if (value == null)
				continue;

			String fieldName = field.getName();

			if (field.getType().equals(String.class) && !String.valueOf(value).isBlank())
				query.append(String.format(" AND b.%s LIKE :%s ", fieldName, fieldName));

			else if (fieldName.startsWith("min")) {
				query.append(fieldName.endsWith("Price")
						? String.format(" AND b.%s >= :%s", fieldName.substring(3).toLowerCase(), fieldName)
						: String.format(" AND ra.value >= :%s", fieldName));
			}

			else if (fieldName.startsWith("max")) {
				query.append(fieldName.endsWith("Price")
						? String.format(" AND b.%s <= :%s", fieldName.substring(3).toLowerCase(), fieldName)
						: String.format(" AND ra.value <= :%s", fieldName));

			}

			else if (Number.class.isAssignableFrom(field.getType())) {
				query.append(String.format(" AND %s = :%s", fieldName, fieldName));
			}

			else if (fieldName.equals("typeCodes") && !List.of(value).isEmpty()) {

				for (int i = 0; i < request.getTypeCodes().size(); ++i) {
					if (i == 0)
						query.append(" AND FIND_IN_SET(:type" + i + ", b.type)");
					else {
						query.append(" OR FIND_IN_SET(:type" + i + ", b.type)");
					}
				}

			}

		}
		return query.toString();

	}

	private void setQueryValue(Query query, BuildingSearchRequest request)
			throws IllegalArgumentException, IllegalAccessException {
		for (Field field : request.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			Object value = field.get(request);
			if (value == null || value.toString().isBlank())
				continue;

			String fieldName = field.getName();

			if (field.getType().equals(String.class))
				query.setParameter(fieldName, "%" + value + "%");

			else if (fieldName.equals("typeCodes")) {

				List<String> typeCodes = (List<String>) value;
				for (int i = 0; i < typeCodes.size(); ++i) {
					query.setParameter("type" + i, typeCodes.get(i));
				}
			} else
				query.setParameter(fieldName, Long.parseLong(String.valueOf(value)));
		}
	}

}