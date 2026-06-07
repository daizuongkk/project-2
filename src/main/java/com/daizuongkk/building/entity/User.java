package com.daizuongkk.building.entity;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "User")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

	@Serial
	private static final long serialVersionUID = -2054386655979281969L;

	public static final String ROLE_MANAGER = "MANAGER";
	public static final String ROLE_EMPLOYEE = "STAFF";
	public static final String ROLE_USER = "USER";

	@Column(name = "username", length = 255, nullable = false)
	private String username;

	@Column(name = "password", length = 128, nullable = false)
	private String password;

	@Column(name = "Active", length = 1, nullable = false)
	private boolean active;

	@Column(name = "userrole", length = 20, nullable = false)
	private String userRole;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Long id;

	@Column(name = "fullname", length = 250, nullable = false)
	private String fullName;

	@Column(name = "phone", length = 10, nullable = false)
	private String phone;

	@Column(name = "email")
	private String email;

	@Lob
	@Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
	private byte[] image;

	private String googleAccountId;

	private String facebookAccountId;

	@ManyToMany(mappedBy = "staffs")
	private List<Building> buildings;

	@ManyToMany(mappedBy = "staffs")
	private List<Customer> customers;

	@OneToMany(mappedBy = "staff")
	private List<Transaction> transactions;

	public User(Long id, String userName, Boolean active, String userRole, String fullName, String phone) {
		this.id = id;
		this.username = userName;
		this.active = active;
		this.userRole = userRole;
		this.fullName = fullName;
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "[" + this.username + "," + this.password + "," + this.userRole + "]";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(this.userRole));
	}

}
