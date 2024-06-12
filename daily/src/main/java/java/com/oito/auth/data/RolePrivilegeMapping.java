package com.oito.auth.data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;

import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(CustomAuditListener.class)
@Getter
@Setter
@Table(name = "role_privilege_mapping")
public class RolePrivilegeMapping implements Auditable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mapping_id")
	private Long mappingId;

	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "privilege_id")
	private Long privilegeId;

	@Embedded
	private Audit audit;

}
