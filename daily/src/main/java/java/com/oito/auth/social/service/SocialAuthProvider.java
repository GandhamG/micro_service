package com.oito.auth.social.service;

import java.util.Map;
import java.util.Optional;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.social.user.SocialUserProfile;

public interface SocialAuthProvider {

	/**
	 * Gets User profile from the provider, using user access token
	 *
	 * @param request
	 * @return
	 */
	Optional<SocialUserProfile> getProfile(final SocialTokenLoginRequest request);

	/**
	 * Build profile from data map
	 *
	 * @param profileData
	 * @return
	 */
	Optional<SocialUserProfile> getProfile(final Map<String, Object> profileData);

	/**
	 *
	 * @returns the corresponding auth provider type
	 */
	AuthProvider getAuthProviderType();
}
