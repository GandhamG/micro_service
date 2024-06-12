package com.oito.auth.common.to;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LiffLoginRequest {

	private String idToken;

	private AuthUserType userType;

}
