/**
 *
 */
package com.oito.auth.validator;

import org.springframework.stereotype.Component;

import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;

/**
 *
 *
 */
@Component
public class EmailUserVaidator {

	public void validateUserEmail(final String email) {

		if (null == email) {
			throw new AuthException(AuthErrorCode.APP_USER_EMAIL_EMPTY);
		}
	}

}
