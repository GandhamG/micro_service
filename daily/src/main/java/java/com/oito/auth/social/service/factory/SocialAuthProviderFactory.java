package com.oito.auth.social.service.factory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.social.service.SocialAuthProvider;

@Component
public class SocialAuthProviderFactory {

	@Autowired
	private List<SocialAuthProvider> authProviders;

	Map<AuthProvider, SocialAuthProvider> factoryMap = new EnumMap<>(AuthProvider.class);

	@PostConstruct
	public void init() {
		authProviders.forEach(w -> factoryMap.put(w.getAuthProviderType(), w));
	}

	public SocialAuthProvider getAuthProvider(final AuthProvider authProvider) {
		return factoryMap.get(authProvider);
	}
}
