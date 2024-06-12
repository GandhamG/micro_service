package com.oito.auth.common.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.oito.auth.common.ErrorAction;
import com.oito.auth.exception.errorcode.AuthErrorCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class SimpleResponse {

	private Status status;
	private String error;
	private AuthErrorCode errorCode;
	private ErrorAction errorAction;

	public SimpleResponse(final Status status) {
		this(status, null, null);
	}

	public SimpleResponse(final Status status, final AuthErrorCode authErrorCode) {
		this(status, authErrorCode.getErrorMessage(), authErrorCode);
	}

	public SimpleResponse(final Status status, final String error, final AuthErrorCode errorCode) {
		this.status = status;
		this.error = error;
		this.errorCode = errorCode;
		this.errorAction = errorCode == null ? null : errorCode.getErrorAction();
	}

	public static SimpleResponse success() {
		return new SimpleResponse(Status.SUCCESS);
	}

	public static SimpleResponse failure(final AuthErrorCode authErrorCode) {
		return new SimpleResponse(Status.FAILURE, authErrorCode);
	}

	public static SimpleResponse failure(final AuthErrorCode authErrorCode, final String errorMessage) {
		return new SimpleResponse(Status.FAILURE, errorMessage, authErrorCode);
	}

	public static SimpleResponse failure(final String errorMessage) {
		return new SimpleResponse(Status.FAILURE, errorMessage, AuthErrorCode.UNKNOWN_EXCEPTION);
	}

}
