package com.daizuongkk.building.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assignmentbuilding")
public class AssignmentBuilding extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "staffid")
	private User staff;

	@ManyToOne
	@JoinColumn(name = "buildingid")
	private Building building;

}
