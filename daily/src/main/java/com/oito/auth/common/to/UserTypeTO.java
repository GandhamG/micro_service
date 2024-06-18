package com.oito.auth.common.to;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.exception.errorcode.AuthErrorCode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTypeTO {

	private Long userTypeId;

	private Long userId;

	private AuthUserType type;

	private boolean enabled;

	private AuthErrorCode errorCode;

	private String errorMessage;

	@JsonIgnore
	private AuditVO audit;

	public UserTypeTO(final Long userId, final AuthUserType type, final boolean enabled) {
		this(null, userId, type, enabled);
	}

	public UserTypeTO(final Long userTypeId, final Long userId, final AuthUserType type, final boolean enabled) {
		this.userId = userId;
		this.userTypeId = userTypeId;
		this.type = type;
		this.enabled = enabled;
	}

	public UserTypeTO(final AuthErrorCode errorCode, final String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
