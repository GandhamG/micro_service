package com.oito.auth.common.to;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SocialLoginRequest {
	private Long userId;
	private String useremail;
	private AuthUserType userType;
	private String socialId;
	private AuthProvider authProvider;
	private boolean friendShipStatusChanged;
	private String fullName;
	private Boolean staySignedIn = Boolean.FALSE;

	public static SocialLoginRequest fromAccountLinkRequest(final UserSignUpRequest signUpRequest,
			final AppUserTO user) {
		final var loginRequest = new SocialLoginRequest();
		loginRequest.setUserId(user.getUserId());
		loginRequest.setUseremail(user.getUseremail());

		loginRequest.setUserType(signUpRequest.getUserType());
		loginRequest.setSocialId(signUpRequest.getSocialId());
		loginRequest.setAuthProvider(signUpRequest.getAuthProvider());
		return loginRequest;
	}
}
