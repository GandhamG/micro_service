package com.oito.auth.v1.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.OTPPhoneVerificationRequestV1;
import com.oito.auth.v1.common.to.UserLoginRequestV1;
import com.oito.auth.v1.common.to.UserLogoutRequestV1;
import com.oito.auth.v1.mapper.AppUserTOV1Mapper;
import com.oito.auth.v1.service.AuthenticationServiceV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("v1/auth")
@Api(tags = "AuthenticationV1")
public class AuthLoginControllerV1 {

	@Autowired
	private AuthenticationServiceV1 userLoginServiceV1;

	@Autowired
	private AppUserTOV1Mapper appUserToMobileUserToMapper;

	@PostMapping("login")
	@ApiOperation(value = "Authenticate users", nickname = "login", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTOV1 login(@RequestBody final UserLoginRequestV1 requestdata,
			@RequestHeader("SESSION") final String sessionId) {
		return userLoginServiceV1.login(requestdata, sessionId);
	}

	@PostMapping("login/phone")
	@ApiOperation(value = "Otp Based Login", nickname = "otpLogin", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTOV1 phoneLogin(@Valid @RequestBody final OTPPhoneVerificationRequestV1 requestData,
			@RequestHeader("SESSION") final String sessionId) {
		final var response = userLoginServiceV1.loginWithPhone(requestData, sessionId);
		return appUserToMobileUserToMapper.toEntity(response);
	}

	@PostMapping("logout")
	@ApiOperation(value = "Logout", nickname = "logout", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse logOut(@RequestHeader("SESSION") final String sessionId,
			@RequestHeader("userId") final Long userId,
			@RequestHeader(HttpHeaders.AUTHORIZATION) final String accessToken,
			@RequestBody final UserLogoutRequestV1 logoutRequest) {
		return userLoginServiceV1.logOut(sessionId, accessToken, logoutRequest, userId);
	}

}
