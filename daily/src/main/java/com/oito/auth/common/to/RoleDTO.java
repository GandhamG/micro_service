package com.oito.auth.common.to;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {

	private Long roleId;

	private String roleCode;

	private String name;

	@JsonIgnore
	private AuditVO audit;

}
