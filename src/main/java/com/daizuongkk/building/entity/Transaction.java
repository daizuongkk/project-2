package com.daizuongkk.building.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {
	private String code;
	private String note;
	private boolean isActive;
	@ManyToOne
	@JoinColumn(name = "customerid")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "staffid")
	private User staff;
}
