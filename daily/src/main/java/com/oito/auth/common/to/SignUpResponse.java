package com.oito.auth.common.to;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oito.auth.common.ErrorAction;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.json.UserTypeTOSerializer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
public class SignUpResponse {

	private Status status;
	private String verificationToken;
	private Long userId;
	private String useremail;

	@JsonSerialize(using = UserTypeTOSerializer.class)
	private Set<UserTypeTO> userTypes;

	private String error;
	private AuthErrorCode errorCode;
	private String maskedUserEmail;
	private ErrorAction errorAction;

	private String warning;

	private String warningCode;

	public SignUpResponse(final Status status, final String verificationToken) {
		this.status = status;
		this.verificationToken = verificationToken;
	}

	public SignUpResponse(final Status status, final String error, final AuthErrorCode errorCode) {
		this.status = status;
		this.error = error;
		this.errorCode = errorCode;
		this.errorAction = this.errorCode.getErrorAction();
	}

	public static SignUpResponse success(final String verificationToken) {
		return new SignUpResponse(Status.SUCCESS, verificationToken);
	}

	public static SignUpResponse failure(final AuthErrorCode authErrorCode) {
		return failure(authErrorCode, authErrorCode.getErrorMessage());
	}

	public static SignUpResponse failure(final AuthErrorCode authErrorCode, final AuthException e) {
		if (e.getErrorMessages().isEmpty()) {
			return failure(authErrorCode, e.getMessage());
		}
		return failure(authErrorCode, e.getErrorMessages().toString());
	}

	public static SignUpResponse failure(final AuthErrorCode errorCode, final String message) {
		return new SignUpResponse(Status.FAILURE, message, errorCode);
	}

	public static SignUpResponse failure(final String message) {
		return new SignUpResponse(Status.FAILURE, message, AuthErrorCode.UNKNOWN_EXCEPTION);
	}

}
