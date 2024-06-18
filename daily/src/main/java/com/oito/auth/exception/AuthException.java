/**
 *
 */
package com.oito.auth.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.common.exception.ApiException;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Dileep
 *
 */
@Getter
@Setter
public class AuthException extends ApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = -2352610323220855125L;

	private final AuthErrorCode authErrorCode;

	private final int statusCode;

	private final List<String> errorMessages;

	/**
	 * @param {@link AuthErrorCode} authErrorCode
	 */
	public AuthException(final AuthErrorCode authErrorCode) {
		super(authErrorCode);
		this.authErrorCode = authErrorCode;
		this.errorMessages = Collections.emptyList();
		this.statusCode = HttpStatus.BAD_REQUEST.value();
	}

	public AuthException(final AuthErrorCode authErrorCode, final List<String> errorMessages) {
		super(authErrorCode, errorMessages);
		if (null == errorMessages) {
			this.errorMessages = Collections.emptyList();
		} else {
			this.errorMessages = errorMessages;
		}
		this.authErrorCode = authErrorCode;
		this.statusCode = HttpStatus.BAD_REQUEST.value();
	}

	public AuthException(final AuthErrorCode authErrorCode, final String message) {
		super(authErrorCode, message);
		this.authErrorCode = authErrorCode;
		this.errorMessages = Collections.emptyList();
		this.statusCode = HttpStatus.BAD_REQUEST.value();
	}

	/**
	 * @param {@link AuthErrorCode} authErrorCode
	 * @param {@link Throwable} cause
	 */
	public AuthException(final AuthErrorCode authErrorCode, final Exception cause) {
		super(authErrorCode, cause);
		this.authErrorCode = authErrorCode;
		this.errorMessages = Collections.emptyList();
		this.statusCode = HttpStatus.BAD_REQUEST.value();
	}

}
