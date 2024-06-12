package com.practice.daily.audit;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Audit {

	@Column(name = "created_by")
	private Long createdBy;

	@Column(name = "created_timestamp")
	private LocalDateTime createdTimestamp;

	@Column(name = "last_updated_by")
	private Long lastUpdatedBy;

	@Column(name = "last_updated_timestamp")
	private LocalDateTime lastUpdatedTimestamp;

	@Column(name = "record_version_no")
	private Integer recordVersionNo;

}
