package com.oito.auth.social.user;

import java.util.Map;

import com.oito.auth.common.AuthProvider;

import lombok.ToString;

@ToString
public class FacebookUserProfile extends SocialUserProfile {
	public FacebookUserProfile(final Map<String, Object> attributes) {
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
		if (attributes.containsKey("picture")) {
			@SuppressWarnings("unchecked")
			final var pictureObj = (Map<String, Object>) attributes.get("picture");
			if (pictureObj.containsKey("data")) {
				@SuppressWarnings("unchecked")
				final var dataObj = (Map<String, Object>) pictureObj.get("data");
				if (dataObj.containsKey("url")) {
					return (String) dataObj.get("url");
				}
			}
		}
		return null;
	}

	@Override
	public AuthProvider getAuthProvider() {
		return AuthProvider.FACEBOOK;
	}
}