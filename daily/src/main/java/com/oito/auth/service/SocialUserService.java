package com.oito.auth.service;

import java.util.Map;
import java.util.Optional;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.LiffLoginRequest;
import com.oito.auth.common.to.OTPLineVerificationRequest;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;

public interface SocialUserService {

	AppUserTO login(SocialLoginRequest loginRequest, String sessionId);

	AppUserTO signUp(UserSignUpRequest requestData, String sessionId);

	AppUserTO loginWithLiff(LiffLoginRequest requestData, String sessionId);

	boolean verifyLineOtp(OTPLineVerificationRequest request);

	Map<String, String> extractToken(String token);

	Optional<AppUserTO> findBySocialId(String socialId, AuthProvider provider);

	void updateSocialInfo(AppUserTO userTO, SocialLoginRequest loginRequest);

}
