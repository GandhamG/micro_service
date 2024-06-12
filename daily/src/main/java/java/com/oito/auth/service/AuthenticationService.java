/**
 *
 */
package com.oito.auth.service;

import java.util.List;
import java.util.Map;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.StaySignedInRequest;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.common.auth.jwt.TokenType;

/**
 * Basic User Services interface
 *
 * @author Dileep
 *
 */
public interface AuthenticationService {

	/**
	 *
	 * Authenticate and provides a user login to IDP through IDP and provides
	 * ID/Access tokens
	 *
	 * {@link AppAuthTokenTO} end user provided data Object
	 */
	AppUserTO authenticateUsernamePassword(UserLoginRequest loginRequest, String sessionId,
			boolean enforceVerification);
	
	AppUserTO authenticateUsernamePasswordWithPartner(final UserLoginRequest loginRequest, final String sessionId,
			final boolean enforceVerification);
	
	AppUserTO authenticateClientGuestLogin(final UserLoginRequest loginRequest);

	void postLogin(AppUserTO userTO, AuthUserType userType, String sessionId, AuthProvider authProvider);

	AppUserTO staySignedIn(StaySignedInRequest requestData);

	void generateAccessToken(AppUserTO userTo, TokenType tokenType, AuthUserType userType,
			Map<String, String> customFields, List<String> audiences, int expiryInMinutes);

	AppUserTO impersonate(UserLoginRequest requestData, String sessionId);

	AppUserTO loginWithPhone(OTPPhoneVerificationRequest requestData, String sessionId);

	AppUserTO loginWithPhone(UserLoginRequest requestData, String sessionId);

	AppUserTOV1 getValidRefreshToken(String token, String sessionId);

}
