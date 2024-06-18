package com.oito.auth.jwt;

import org.springframework.stereotype.Component;

@Component
public class TokenHandler {

	private static final String BEARER = "Bearer ";

	public String getAccessTokenFromAuthorization(final String authorizationHeader) {
		return authorizationHeader.substring(BEARER.length()).trim();
	}
}
