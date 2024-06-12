package com.oito.auth.dao.audit;

import com.oito.auth.data.Audit;

public interface Auditable {

	Audit getAudit();

	void setAudit(Audit audit);
}