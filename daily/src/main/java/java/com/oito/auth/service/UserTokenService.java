package com.oito.auth.service;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.UserTokenTO;

public interface UserTokenService {

	UserTokenTO postLoginRefreshToken(String macId, Long userId, AuthUserType userType);

	UserTokenTO getValidRefreshToken(String token);

}
