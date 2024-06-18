package com.oito.auth.common.to;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SocialTokenLoginRequest {
	private String socialAccessToken;
	private AuthProvider authProvider;
	private AuthUserType userType;
}
