package com.practice.daily.audit;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuditListener {

	private static final Long DEFAULT_USER = Long.valueOf(-90L);

	@PrePersist
	public void setCreatedOn(final Auditable auditable) {
		var audit = auditable.getAudit();

		if (audit == null) {
			audit = new Audit();
			auditable.setAudit(audit);
		}
		final var now = LocalDateTime.now();
		audit.setCreatedTimestamp(now);
		audit.setLastUpdatedTimestamp(now);
		audit.setRecordVersionNo(NumberUtils.INTEGER_ZERO);
		audit.setCreatedBy(DEFAULT_USER);
		audit.setLastUpdatedBy(DEFAULT_USER);
	}

	@PreUpdate
	public void setUpdatedOn(final Auditable auditable) {
		try {
			final var audit = auditable.getAudit();
			audit.setLastUpdatedTimestamp(LocalDateTime.now());
			audit.setRecordVersionNo(Integer.valueOf(audit.getRecordVersionNo().intValue() + 1));
			audit.setLastUpdatedBy(DEFAULT_USER);
		} catch (final Exception e) {
			log.error("error during updating Audit!!", e);
		}
	}

}