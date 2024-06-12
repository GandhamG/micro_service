package com.oito.auth.social.user;

import java.io.Serializable;
import java.util.Map;

import com.oito.auth.common.AuthProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public abstract class SocialUserProfile implements Serializable {

	protected Map<String, Object> attributes;

	public abstract String getSocialId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();

	public abstract AuthProvider getAuthProvider();

}