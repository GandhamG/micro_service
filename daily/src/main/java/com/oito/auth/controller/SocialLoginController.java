package com.oito.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.LiffLoginRequest;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.SocialUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth/social")
@Slf4j
public class SocialLoginController {

	@Autowired
	private SocialUserService userService;

	@PostMapping("/login")
	public Object login(@RequestBody final SocialLoginRequest requestData,
			@RequestHeader("SESSION") final String sessionId) {
		log.info("Social service login requested for the user {}", requestData);
		try {
			return userService.login(requestData, sessionId);
		} catch (final AuthException e) {
			log.error("Login failed for the user with auth exception {} ", requestData);
			return formErrorTO(e, e.getAuthErrorCode());
		} catch (final Exception e) {
			log.error("Login failed for the user with unknown exception {}", requestData);
			return formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@GetMapping("token/{token}")
	public Map<String, String> tokenInfo(@PathVariable final String token) {
		return userService.extractToken(token);
	}

	@PostMapping("/login/liff")
	public AppUserTO loginWithLiff(@RequestBody final LiffLoginRequest requestData,
			@RequestHeader("SESSION") final String sessionId) {
		try {
			return userService.loginWithLiff(requestData, sessionId);
		} catch (final AuthException e) {
			log.error("Liff Login failed for the user with auth exception {} ", requestData);
			return formErrorTO(e, e.getAuthErrorCode());
		}

	}

	@PostMapping("/signup")
	public Object signUp(@RequestBody final UserSignUpRequest requestData,
			@RequestHeader("SESSION") final String sessionId) {
		log.info("Social service sign up requested for the user {}", requestData);
		try {
			return userService.signUp(requestData, sessionId);
		} catch (final AuthException e) {
			log.error("Login failed for the user with auth exception {} ", requestData);
			return formErrorTO(e, e.getAuthErrorCode());
		} catch (final Exception e) {
			log.error("Login failed for the user with unknown exception {}", requestData);
			return formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	private AppUserTO formErrorTO(final Exception e, final AuthErrorCode errorCode) {
		log.error("ErrorCode {}", errorCode);
		log.error("Error", e);
		final var userTO = new AppUserTO();
		userTO.setError(e.getMessage());
		userTO.setErrorCode(errorCode.getErrorCode());
		userTO.setErrorAction(errorCode.getErrorAction());
		return userTO;
	}
}
