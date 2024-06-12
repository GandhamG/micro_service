package com.oito.auth.common.to;

import java.util.Set;

import com.oito.auth.exception.errorcode.AuthErrorCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteResponse {
	private UserDeleteRequestStatus status;
	private String fullName;
	private String useremail;
	private String reason;
	private Long userId;
	private Set<UserTypeTO> userTypes;
	private AuthErrorCode errorCode;
	private String lang;
	private String anonymousEmail;
	private String phoneNumber;
}