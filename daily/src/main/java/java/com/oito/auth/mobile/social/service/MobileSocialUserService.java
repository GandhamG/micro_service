package com.oito.auth.mobile.social.service;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SocialTokenLoginRequest;
import com.oito.auth.common.to.SocialTokenSignupRequest;

public interface MobileSocialUserService {

	/**
	 * Login with social token
	 *
	 * @param loginRequest
	 * @return
	 */
	AppUserTO login(SocialTokenLoginRequest tokenLoginRequest, final String sessionId);

	AppUserTO signup(SocialTokenSignupRequest tokenSignupRequest, final String sessionId);

}
