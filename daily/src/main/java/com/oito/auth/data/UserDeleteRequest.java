/**
 *
 */
package com.oito.auth.data;

import java.time.LocalDateTime;

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

import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * User entity representation of database user model
 *
 *
 *
 */
@Entity
@EntityListeners(CustomAuditListener.class)
@Table(name = "user_delete_request")
@Getter
@Setter
public class UserDeleteRequest implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "processed_timestamp")
	private LocalDateTime processedTimestamp;

	@Column(name = "reason")
	private String reason;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private UserDeleteRequestStatus status = UserDeleteRequestStatus.OPEN;

	@Embedded
	private Audit audit;

}
