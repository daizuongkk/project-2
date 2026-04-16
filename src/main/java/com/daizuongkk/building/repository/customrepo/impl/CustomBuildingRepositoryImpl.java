package com.daizuongkk.building.repository.customrepo.impl;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.daizuongkk.building.builder.BuildingSearchBuilder;
import com.daizuongkk.building.entity.Building;
import com.daizuongkk.building.repository.customrepo.CustomBuildingRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomBuildingRepositoryImpl implements CustomBuildingRepository {
	@PersistenceContext
	private EntityManager entityManager;

	public List<Building> findBuildings(BuildingSearchBuilder request) {
		StringBuilder queryStr = new StringBuilder("SELECT DISTINCT b.* FROM building b ");
		buildJoinClause(request, queryStr);
		queryStr.append(" WHERE 1=1 ");
		buildNormalClause(request, queryStr);
		buildSpecialClause(request, queryStr);
		Query query = entityManager.createNativeQuery(queryStr.toString(), Building.class);
		setQueryValue(query, request);
		return query.getResultList();
	}

	private String buildJoinClause(BuildingSearchBuilder request, StringBuilder query) {

		if (request.getMaxRentArea() != null || request.getMinRentArea() != null)
			query.append("JOIN rentarea ra ON ra.buildingid = b.id ");

		if (request.getStaffId() != null)
			query.append("JOIN assignmentbuilding ab ON ab.buildingid = b.id ");

		return query.toString();
	}

	private String buildNormalClause(BuildingSearchBuilder request, StringBuilder query) {
		try {
			for (Field field : request.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(request);
				if (value == null)
					continue;

				String fieldName = field.getName();

				if (fieldName.equals("staffId") || fieldName.equals("typeCodes") || fieldName.endsWith("rentArea")
						|| fieldName.endsWith("rentPrice"))
					continue;

				if (Number.class.isAssignableFrom(field.getClass())) {
					query.append(" AND b.").append(fieldName.toLowerCase()).append(" = :").append(fieldName);
				} else {
					query.append(" AND b.").append(fieldName.toLowerCase()).append(" LIKE :").append(fieldName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return query.toString();

	}

	private String buildSpecialClause(BuildingSearchBuilder request, StringBuilder query) {
		if (request.getMinRentArea() != null) {
			query.append(" AND ra.value >= :minRentArea");
		}

		if (request.getMaxRentArea() != null) {
			query.append(" AND ra.value <= :maxRentArea");
		}

		if (request.getMinRentPrice() != null) {
			query.append(" AND b.price >= :minPrice");
		}

		if (request.getMinRentPrice() != null) {
			query.append(" AND b.price <= :maxPrice");
		}

		if (request.getStaffId() != null) {
			query.append(" AND ab.staffid = :staffId");
		}

		List<String> typeCodes = request.getTypeCodes();

		if (typeCodes != null && !typeCodes.isEmpty()) {
			query.append(" AND ( ");
			for (int i = 0; i < request.getTypeCodes().size(); ++i) {
				if (i > 0)
					query.append(" OR ");
				query.append(" FIND_IN_SET(:type").append(i).append(", b.type)");
			}
			query.append(")");
		}

		return query.toString();

	}

	private void setQueryValue(Query query, BuildingSearchBuilder request) {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

// if (field.getType().equals(String.class) && !String.valueOf(value).isBlank())
// query.append(String.format(" AND b.%s LIKE :%s ", fieldName, fieldName));

// else if (fieldName.startsWith("min")) {
// query.append(fieldName.endsWith("Price")
// ? String.format(" AND b.%s >= :%s", fieldName.substring(3).toLowerCase(),
// fieldName)
// : String.format(" AND ra.value >= :%s", fieldName));
// }

// else if (fieldName.startsWith("max")) {
// query.append(fieldName.endsWith("Price")
// ? String.format(" AND b.%s <= :%s", fieldName.substring(3).toLowerCase(),
// fieldName)
// : String.format(" AND ra.value <= :%s", fieldName));

// }

// else if (Number.class.isAssignableFrom(field.getType())) {
// query.append(String.format(" AND %s = :%s", fieldName, fieldName));
// }

// else if (fieldName.equals("typeCodes") && !List.of(value).isEmpty()) {
// query.append(" AND ( ");
// for (int i = 0; i < request.getTypeCodes().size(); ++i) {
// if (i > 0)
// query.append(" OR ");
// query.append(" FIND_IN_SET(:type").append(i).append(", b.type)");
// }
// query.append(")");
// }