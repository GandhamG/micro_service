/**
 *
 */
package com.oito.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.StaySignedInRequest;
import com.oito.auth.common.to.UserLoginAttemptVO;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.AuthenticationService;
import com.oito.auth.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dileep
 *
 */

@RestController
@RequestMapping("auth")
@Slf4j
@Api(tags = "User Authentication")
public class AuthLoginController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserService userService;

	/**
	 * User login authentication using a username and password, and the relevant
	 * tokens would be granted
	 *
	 * @param authenticationRequest
	 * @return
	 */

	@PostMapping("login")
	@ApiOperation(value = "Authenticate users", nickname = "login", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTO login(@RequestBody final UserLoginRequest requestdata,
			@RequestHeader("SESSION") final String sessionId) {
		return executeLogin(requestdata, sessionId, true);
	}

	@PostMapping("login/partner")
	@ApiOperation(value = "Authenticate users with partner", nickname = "loginPartner", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTO partnerLogin(@RequestBody final UserLoginRequest requestdata,
			@RequestHeader("SESSION") final String sessionId) {
		return executePartnerLogin(requestdata, sessionId, true);
	}

	@PostMapping("guestlogin/client")
	@ApiOperation(value = "Fetch client details for guest", nickname = "guestloginClient", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTO clientGuestLogin(@RequestBody final UserLoginRequest requestdata) {
		return executeClientGuestLogin(requestdata);
	}

	@PostMapping("login/phone")
	@ApiOperation(value = "Otp Based Login", nickname = "otpLogin", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTO phoneLogin(@RequestBody final OTPPhoneVerificationRequest requestData,
			@RequestHeader("SESSION") final String sessionId) {
		try {
			return authenticationService.loginWithPhone(requestData, sessionId);
		} catch (final AuthException e) {
			log.error("Liff Login failed for the user with auth exception {} ", requestData);
			return formErrorTO(e, e.getAuthErrorCode());
		}

	}

	@PostMapping("impersonate")
	@ApiOperation(value = "Impersonate users", nickname = "impersonate", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTO impersonate(@RequestBody final UserLoginRequest requestdata,
			@RequestHeader("SESSION") final String sessionId) {
		try {
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$Authentication service impersonate requested for the user {}",
					requestdata);
			return authenticationService.impersonate(requestdata, sessionId);
		} catch (final AuthException e) {
			log.error("Login failed for the user with auth exception {} ", requestdata);
			return formErrorTO(e, e.getAuthErrorCode());
		} catch (final Exception e) {
			log.error("Login failed for the user with unknown exception {}", requestdata);
			return formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	private AppUserTO executeLogin(final UserLoginRequest requestdata, final String sessionId,
			final boolean enforceVerification) {
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$Authentication service login requested for the user {}", requestdata);
		try {
			return authenticationService.authenticateUsernamePassword(requestdata, sessionId, enforceVerification);
		} catch (final AuthException e) {
			log.error("Login failed for the user with auth exception {} ", requestdata);
			return formErrorTO(e, e.getAuthErrorCode());
		} catch (final Exception e) {// TODO make it generic/use custom exception
			log.error("Login failed for the user with unknown exception {}", requestdata);
			return formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	private AppUserTO executePartnerLogin(final UserLoginRequest requestdata, final String sessionId,
			final boolean enforceVerification) {
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$Authentication service login with Partner requested for the user {}",
				requestdata);
		return authenticationService.authenticateUsernamePasswordWithPartner(requestdata, sessionId,
				enforceVerification);
	}

	private AppUserTO executeClientGuestLogin(final UserLoginRequest requestdata) {
		log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$Authentication service Guest login with Client requested for {}",
				requestdata);
		return authenticationService.authenticateClientGuestLogin(requestdata);
	}

	@PostMapping("stay-signed-in")
	public Object staySignedIn(@RequestBody final StaySignedInRequest requestdata) {
		try {
			log.info("Stay signed in triggered for request {}", requestdata);
			return authenticationService.staySignedIn(requestdata);
		} catch (final AuthException e) {
			log.error("Login failed for the user with auth exception {} ", requestdata);
			return formErrorTO(e, e.getAuthErrorCode());
		}
	}

	private AppUserTO formErrorTO(final Exception e, final AuthErrorCode errorCode) {
		log.error("Error", e);
		final var userTO = new AppUserTO();
		userTO.setError(e.getMessage());
		userTO.setErrorCode(errorCode.getErrorCode());
		userTO.setErrorAction(errorCode.getErrorAction());
		return userTO;
	}

	@PostMapping("logout")
	@ApiOperation(value = "Logout", nickname = "logout", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse logOut(@RequestHeader("SESSION") final String sessionId,
			@RequestHeader("userId") final Long userId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) final String accessToken) {
		return userService.logOut(sessionId, userId, accessToken);
	}

	@PostMapping("login_attempts")
	public UserLoginAttemptVO loginAttempts(@RequestBody final UserLoginAttemptVO loginAttemptVO) {
		return userService.executeLoginAttempts(loginAttemptVO);
	}

}
