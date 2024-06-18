package com.oito.auth.mobile.social.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.common.to.SocialTokenSignupRequest;
import com.oito.auth.mobile.social.service.MobileSocialUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("apps/v1/auth/social")
@Slf4j
public class MobileSocialLoginController {

	@Autowired
	private MobileSocialUserService userService;

	@PostMapping("login")
	public Object login(@RequestBody final SocialTokenLoginRequest requestData,
			@RequestHeader("SESSION") final String sessionId) {
		log.info("Social login through mobile is requested for the user {}", requestData);
		return userService.login(requestData, sessionId);
	}

	@PostMapping("signup")
	public Object signup(@RequestBody final SocialTokenSignupRequest requestData,
			@RequestHeader(name = "SESSION", defaultValue = StringUtils.EMPTY) final String sessionId) {
		return userService.signup(requestData, sessionId);
	}

}
