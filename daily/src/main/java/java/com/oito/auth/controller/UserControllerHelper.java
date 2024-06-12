package com.oito.auth.controller;

import org.springframework.stereotype.Component;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;

@Component
public class UserControllerHelper {

	public AppUserTO formErrorTO(final Exception e, final AuthErrorCode errorCode) {
		final var userTO = new AppUserTO();
		userTO.setError(e.getMessage());
		userTO.setErrorCode(errorCode.getErrorCode());
		userTO.setErrorAction(errorCode.getErrorAction());
		return userTO;
	}

	public AppUserTO formErrorTO(final AuthException e) {
		final var userTO = formErrorTO(e, e.getAuthErrorCode());
		if (!e.getErrorMessages().isEmpty()) {
			userTO.setError(e.getErrorMessages().toString());
		}
		return userTO;
	}
}
