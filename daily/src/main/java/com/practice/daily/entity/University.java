package com.practice.daily.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.daily.audit.Audit;
import com.practice.daily.audit.Auditable;
import com.practice.daily.audit.CustomAuditListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "university", uniqueConstraints = @UniqueConstraint(columnNames = { "email" }))
@Setter
@Getter
@EntityListeners(CustomAuditListener.class)
public class University implements Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "u_id")
	private Long id;

	@Column(name = "u_name")
	private String name;

	@Column(name = "email")
	private String email;

	/*
	 * bidirectional Relationship u_id will foreign key in the student table
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "u_id")
	@JsonIgnore
	private List<Student> student;

	@Embedded
	private Audit audit;
}
