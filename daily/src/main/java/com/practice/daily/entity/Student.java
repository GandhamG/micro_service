package com.practice.daily.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import com.practice.daily.audit.Audit;
import com.practice.daily.audit.Auditable;
import com.practice.daily.audit.CustomAuditListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student", uniqueConstraints = @UniqueConstraint(columnNames = { "email" }))
@Getter
@Setter
@EntityListeners(CustomAuditListener.class)
public class Student implements Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "s_id")
	private Long studentId;

	@Column(name = "name")
	private String name;

	@Audited
	@Column(name = "email")
	private String email;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "u_id", updatable = false, insertable = true)
	private University university;

	@Embedded
	private Audit audit;

}
