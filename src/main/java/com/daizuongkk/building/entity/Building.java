package com.daizuongkk.building.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "building")
public class Building {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "street", length = 255, nullable = false)
	private String street;

	@Column(name = "ward", length = 255, nullable = false)
	private String ward;

	@Column(name = "structure", length = 255)
	private String structure;

	@Column(name = "district", length = 255, nullable = false)
	private String district;

	@Column(name = "numberofbasement")
	private Long numberOfBasement;

	@Column(name = "floorarea")
	private Long floorArea;

	@Column(name = "direction")
	private String direction;

	@Column(name = "level")
	private String level;

	@Column(name = "rentprice", nullable = false)
	private Long price;

	@Column(name = "rentpricedescription")
	private String rentPriceDescription;

	@Column(name = "servicefee", length = 255)
	private String serviceFee;

	@Column(name = "carfee", length = 255)
	private String carFee;

	@Column(name = "motofee", length = 255)
	private String motoFee;

	@Column(name = "overtimefee", length = 255)
	private String overtimeFee;

	@Column(name = "waterfee", length = 255)
	private String waterFee;

	@Column(name = "electricityfee", length = 255)
	private String electricityFee;

	@Column(name = "deposit", length = 255)
	private String deposit;

	@Column(name = "payment", length = 255)
	private String payment;

	@Column(name = "renttime", length = 255)
	private String rentTime;

	@Column(name = "decorationtime", length = 255)
	private String decorationTime;

	@Column(name = "brokeragefee")
	private Double brokerageFee;

	@Column(name = "managername")
	private String managerName;

	@Column(name = "managerphone")
	private String managerPhone;

	@Column(name = "rentarea")
	@OneToMany(mappedBy = "building", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private List<RentArea> rentArea = new ArrayList<>();

	@Column(name = "type")
	private String type;

	@Column(name = "note")
	private String note;

	@ManyToMany
	@JoinTable(name = "assignmentbuilding", joinColumns = @JoinColumn(name = "buildingid"), inverseJoinColumns = @JoinColumn(name = "staffid"))
	private List<User> staffs = new ArrayList<>();

	@Lob
	@Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
	private byte[] image;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createddate", nullable = false)
	private Date createdDate;

	@Column(name = "createdby")
	@CreatedBy
	private String createdBy;

	@Column(name = "modifieddate")
	@LastModifiedDate
	private Date modifiedDate;

	@Column(name = "modifiedby")
	@LastModifiedBy
	private String modifiedBy;

}
