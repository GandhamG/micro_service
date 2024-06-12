package com.oito.auth.social.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.proxy.GoogleProxy;
import com.oito.auth.social.service.SocialAuthProvider;
import com.oito.auth.social.user.GoogleUserProfile;
import com.oito.auth.social.user.SocialUserProfile;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GoogleSocialAuthProvider implements SocialAuthProvider {

	@Autowired
	private GoogleProxy googleProxy;

	@Override
	public Optional<SocialUserProfile> getProfile(final SocialTokenLoginRequest request) {
		final var profileData = googleProxy.getProfile(request.getSocialAccessToken());
		return Optional.ofNullable(profileData).map(GoogleUserProfile::new);
	}

	@Override
	public Optional<SocialUserProfile> getProfile(final Map<String, Object> profileData) {
		return Optional.ofNullable(profileData).map(GoogleUserProfile::new);
	}

	@Override
	public AuthProvider getAuthProviderType() {
		return AuthProvider.GOOGLE;
	}
}
