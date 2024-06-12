package com.oito.auth.common.to;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SocialTokenSignupRequest {

	private String socialProfileToken;
	private String phoneNo;
	private String phoneCountryCode;
	private AuthUserType userType;

}
