package com.oito.auth.common.to;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivilegeDTO {

	private Long privilegeId;

	private String resourceCode;

	private String accessCode;

	@JsonIgnore
	private AuditVO audit;
}
