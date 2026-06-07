package com.daizuongkk.building.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "customer")
@Getter
@Setter
@Entity
@SuperBuilder
@RequiredArgsConstructor
public class Customer extends BaseEntity {

	@Column(name = "fullname", nullable = false)
	private String fullName;
	@Column(nullable = false)
	private String phone;
	private String email;

	@ManyToMany
	@JoinTable(name = "assignmentcustomer", joinColumns = @JoinColumn(name = "customerid"), inverseJoinColumns = @JoinColumn(name = "staffid"))
	private List<User> staffs;

	@OneToMany(mappedBy = "customer")
	private List<Transaction> transactions;
	@Column(name = "companyname")
	private String companyName;
	private String demand;
	private String status;
	private boolean isActive;

}
