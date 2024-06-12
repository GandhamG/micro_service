package com.oito.auth.data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(CustomAuditListener.class)
@Table(name = "user_type")
@Getter
@Setter
public class UserType implements Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_type_id")
	private Long userTypeId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private AuthUserType type;

	@Column(name = "enabled")
	private boolean enabled;

	@Embedded
	private Audit audit;

}
