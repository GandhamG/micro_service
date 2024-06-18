package com.oito.auth.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.service.AuthenticationService;
import com.oito.auth.v1.common.to.AppUserTOV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user/token")
@Api(tags = "AuthenticationV1")
@Slf4j
public class UserTokenController {

	@Autowired
	private AuthenticationService authenticationService;

	@ApiOperation(value = "Refresh Access Token", notes = "This API will help us to refresh the access the token", nickname = "RefreshToken", code = 200, httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@GetMapping("refresh/{refreshToken}")
	public AppUserTOV1 getRefreshToken(@PathVariable final String refreshToken,
			@RequestHeader(name = "SESSION", required = false, defaultValue = StringUtils.EMPTY) final String sessionId) {
		log.info("User token passed {}", refreshToken);
		return authenticationService.getValidRefreshToken(refreshToken, sessionId);

	}

}
