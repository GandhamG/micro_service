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
@Table(name = "user_metadata")
@Getter
@Setter
public class UserMetadata implements Auditable {

	@Id
	@Column(name = "metadata_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long metadataId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "code")
	private String code;

	@Column(name = "value")
	private String value;

	@Embedded
	private Audit audit;

}
