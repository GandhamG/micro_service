package com.oito.auth.social.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.proxy.FacebookProxy;
import com.oito.auth.social.service.SocialAuthProvider;
import com.oito.auth.social.user.FacebookUserProfile;
import com.oito.auth.social.user.SocialUserProfile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FacebookSocialAuthProvider implements SocialAuthProvider {

	private static final String FIELD_LIST = "id,name,picture,email";

	@Autowired
	private FacebookProxy facebookProxy;

	@Override
	public Optional<SocialUserProfile> getProfile(final SocialTokenLoginRequest request) {
		final var profileData = facebookProxy.getProfile(FIELD_LIST, request.getSocialAccessToken());
		return Optional.ofNullable(profileData).map(FacebookUserProfile::new);
	}

	@Override
	public Optional<SocialUserProfile> getProfile(final Map<String, Object> profileData) {
		return Optional.ofNullable(profileData).map(FacebookUserProfile::new);
	}

	@Override
	public AuthProvider getAuthProviderType() {
		return AuthProvider.FACEBOOK;
	}
}
