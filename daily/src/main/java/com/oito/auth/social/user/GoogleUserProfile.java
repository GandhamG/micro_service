package com.oito.auth.social.user;

import java.util.Map;

import com.oito.auth.common.AuthProvider;

import lombok.ToString;

@ToString
public class GoogleUserProfile extends SocialUserProfile {

	public GoogleUserProfile(final Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getSocialId() {
		return (String) attributes.get("id");
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) attributes.get("picture");
	}

	@Override
	public AuthProvider getAuthProvider() {
		return AuthProvider.GOOGLE;
	}

}