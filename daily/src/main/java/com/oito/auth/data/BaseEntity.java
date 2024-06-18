package com.oito.auth.data;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.math.NumberUtils;

@MappedSuperclass
public class BaseEntity {

	private static final Long DEFAULT_USER = Long.valueOf(-90L);

	@Column(name = "created_by")
	private Long createdById;

	@Column(name = "last_updated_by")
	private Long lastUpdatedById;

	@Column(name = "created_timestamp")
	private Instant createdTimestamp;

	@Column(name = "last_updated_timestamp")
	private Instant lastUpdatedTimestamp;

	@Column(name = "record_version_no")
	private Integer recordVersionNo;

	@PrePersist
	protected void preCreate() {
		createdById = DEFAULT_USER;
		lastUpdatedById = DEFAULT_USER;
		createdTimestamp = Instant.now();
		lastUpdatedTimestamp = createdTimestamp;
		recordVersionNo = NumberUtils.INTEGER_ZERO;
	}

	@PreUpdate
	protected void preUdate() {
		lastUpdatedTimestamp = Instant.now();
		recordVersionNo = Integer.valueOf(recordVersionNo.intValue() + 1);
	}

}
