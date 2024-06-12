package com.oito.auth.mobile.social.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.common.to.SocialTokenSignupRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.SocialProfileLoginRequestMapper;
import com.oito.auth.mapper.SocialProfileSignupRequestMapper;
import com.oito.auth.mapper.SocialTokenLoginRequestMapper;
import com.oito.auth.mapper.SocialTokenSignupRequestMapper;
import com.oito.auth.mobile.social.service.MobileSocialUserService;
import com.oito.auth.service.SocialUserService;
import com.oito.auth.social.jwt.SocialTokenHandler;
import com.oito.auth.social.service.factory.SocialAuthProviderFactory;
import com.oito.auth.social.user.SocialUserProfile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MobileSocialLoginServiceImpl implements MobileSocialUserService {

	@Autowired
	private SocialAuthProviderFactory socialAuthProviderFactory;

	@Autowired
	private SocialUserService socialUserService;

	@Autowired
	private SocialTokenHandler socialTokenHandler;

	@Autowired
	private SocialTokenLoginRequestMapper loginMapper;

	@Autowired
	private SocialTokenSignupRequestMapper signupMapper;

	@Autowired
	private SocialProfileSignupRequestMapper profileSignupMapper;

	@Autowired
	private SocialProfileLoginRequestMapper profileLoginMapper;

	@Override
	public AppUserTO login(final SocialTokenLoginRequest tokenLoginRequest, final String sessionId) {
		/* Gets User Profile from social */
		final var socialProfile = socialAuthProviderFactory.getAuthProvider(tokenLoginRequest.getAuthProvider())
				.getProfile(tokenLoginRequest).orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));

		/*
		 * -If user exists in our system, the do login ,update the user and give back
		 * the application token
		 *
		 * -If no user in our system , then give back a no user response , along with
		 * the encrypted social profile token
		 */
		final var userTo = socialUserService.login(toLoginRequest(tokenLoginRequest, socialProfile), sessionId);
		return Optional.of(userTo).filter(u -> !u.isEmpty()).map(usr -> usr)
				.orElse(formNoUserResponseWithSocialProfileToken(socialProfile));
	}

	@Override
	public AppUserTO signup(final SocialTokenSignupRequest tokenSignupRequest, final String sessionId) {
		final var socialProfile = socialTokenHandler.decodeToken(tokenSignupRequest.getSocialProfileToken());
		return socialProfile.map(s -> socialUserService.signUp(toSignupRequest(tokenSignupRequest, s), sessionId))
				.orElse(AppUserTO.empty());
	}

	private AppUserTO formNoUserResponseWithSocialProfileToken(final SocialUserProfile socialProfile) {
		final var userTO = new AppUserTO();
		final var errorCode = AuthErrorCode.USER_NOT_FOUND;
		userTO.setError(errorCode.getErrorMessage());
		userTO.setErrorCode(errorCode.getErrorCode());
		userTO.setErrorAction(errorCode.getErrorAction());
		userTO.setSocialProfileToken(socialTokenHandler.generateToken(socialProfile));
		return userTO;
	}

	private SocialLoginRequest toLoginRequest(final SocialTokenLoginRequest tokenLoginRequest,
			final SocialUserProfile socialProfile) {
		final var loginRequest = loginMapper.toEntity(tokenLoginRequest);
		profileLoginMapper.mapEntity(socialProfile, loginRequest);
		return loginRequest;
	}

	private UserSignUpRequest toSignupRequest(final SocialTokenSignupRequest tokenSignupRequest,
			final SocialUserProfile socialProfile) {
		final var signupRequest = signupMapper.toEntity(tokenSignupRequest);
		profileSignupMapper.mapEntity(socialProfile, signupRequest);
		return signupRequest;
	}

}
