package com.oito.auth.v1.service;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.OTPPhoneVerificationRequestV1;
import com.oito.auth.v1.common.to.UserLoginRequestV1;
import com.oito.auth.v1.common.to.UserLogoutRequestV1;

public interface AuthenticationServiceV1 {
	AppUserTOV1 login(UserLoginRequestV1 mobileUserLoginRequest, String sessionId);

	SimpleResponse logOut(String sessionId, String accessToken, UserLogoutRequestV1 logoutRequest, final Long userId);

	AppUserTO loginWithPhone(OTPPhoneVerificationRequestV1 requestData, String sessionId);

}
