package com.oito.auth.dao.audit;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.data.Audit;
import com.oito.auth.service.impl.UserContextService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomAuditListener {

	@Autowired
	private UserContextService userContextService;

	@PrePersist
	public void setCreatedOn(final Auditable auditable) {
		var audit = auditable.getAudit();

		if (audit == null) {
			audit = new Audit();
			auditable.setAudit(audit);
		}
		final var now = LocalDateTime.now();
		final var userId = userContextService.getUserIdNullSafe();
		audit.setCreatedTimestamp(now);
		audit.setLastUpdatedTimestamp(now);
		audit.setRecordVersionNo(NumberUtils.INTEGER_ZERO);
		audit.setCreatedBy(userId);
		audit.setLastUpdatedBy(userId);
	}

	@PreUpdate
	public void setUpdadtedOn(final Auditable auditable) {
		try {
			final var audit = auditable.getAudit();
			audit.setLastUpdatedTimestamp(LocalDateTime.now());
			audit.setRecordVersionNo(Integer.valueOf(audit.getRecordVersionNo().intValue() + 1));
			audit.setLastUpdatedBy(userContextService.getUserIdNullSafe());
		} catch (final Exception e) {
			log.error("error during updating Audit!!", e);
		}
	}

}