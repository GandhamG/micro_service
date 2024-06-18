package com.oito.auth.social.jwt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oito.auth.common.AuthProvider;
import com.oito.auth.social.service.factory.SocialAuthProviderFactory;
import com.oito.auth.social.user.SocialUserProfile;
import com.oito.common.auth.jwt.JWETokenHandler;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SocialTokenHandler {

	@Value("${oito.social.jwt.expiry-in-minutes:1440}")
	private int tokenExpiryInMinutes;

	@Autowired
	private JWETokenHandler jweTokenHandler;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private SocialAuthProviderFactory socialAuthProviderFactory;

	public String generateToken(final SocialUserProfile socialProfile) {
		socialProfile.getAttributes().put("authProvider", socialProfile.getAuthProvider());
		return jweTokenHandler.generateJWT(objectMapper.convertValue(socialProfile.getAttributes(), Map.class),
				List.of(), tokenExpiryInMinutes, socialProfile.getName());
	}

	public Optional<SocialUserProfile> decodeToken(final String SocialProfileToken) {
		final Map<String, Object> profileData = new HashMap<>(jweTokenHandler.decodeToken(SocialProfileToken));
		return Optional.of(profileData).map(p -> p.get("authProvider")).map(a -> AuthProvider.valueOf((String) a))
				.map(s -> socialAuthProviderFactory.getAuthProvider(s).getProfile(profileData))
				.orElse(Optional.empty());

	}
}
