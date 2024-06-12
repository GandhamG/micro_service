package com.oito.auth.common.to;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
public class UserLoginRequest {

	private String useremail;

	private String userName;

	private String password;

	private String phoneNo;

	private AuthUserType userType;

	private String impersonateUserEmail;

	private Boolean staySignedIn = Boolean.FALSE;
	
	private String clientId;

	private String secretKey;

	private Map<String, String> tokenParams;
	
	@JsonIgnore
	private Map<String, String> customFields;

	@JsonIgnore
	private String macId;

	@JsonIgnore
	private Long otpId;

	@JsonIgnore
	private String otp;

	@JsonIgnore
	private boolean generateRefreshToken;

}
