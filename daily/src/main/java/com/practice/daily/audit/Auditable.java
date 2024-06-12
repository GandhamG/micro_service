package com.practice.daily.audit;

public interface Auditable {

	Audit getAudit();

	void setAudit(Audit audit);
}