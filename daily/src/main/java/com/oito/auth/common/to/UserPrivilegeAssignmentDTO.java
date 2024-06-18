package com.oito.auth.common.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.UserPrivilegeAssignmentType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPrivilegeAssignmentDTO {

	private Long assignmentId;

	private Long userId;

	private UserPrivilegeAssignmentType type;

	private Long typeId;

	@JsonIgnore
	private AuditVO audit;

	public UserPrivilegeAssignmentDTO(final Long userId, final UserPrivilegeAssignmentType type, final Long typeId) {
		this.userId = userId;
		this.type = type;
		this.typeId = typeId;
	}
}
