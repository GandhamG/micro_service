package com.oito.auth.v1.service;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.Status;
import com.oito.auth.dao.UserTypeDAO;
import com.oito.auth.dao.repository.UserTokenRepository;
import com.oito.auth.service.AuthenticationService;
import com.oito.auth.service.UserService;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.OTPPhoneVerificationRequestV1;
import com.oito.auth.v1.common.to.UserLoginRequestV1;
import com.oito.auth.v1.common.to.UserLogoutRequestV1;
import com.oito.auth.v1.mapper.AppUserTOV1Mapper;
import com.oito.auth.v1.mapper.OTPLoginV1Mapper;
import com.oito.auth.v1.mapper.UserLoginRequestV1Mapper;
import com.oito.common.auth.jwt.JWETokenHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationServiceImplV1 implements AuthenticationServiceV1 {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserLoginRequestV1Mapper loginRequestMapper;

	@Autowired
	private AppUserTOV1Mapper appUserToMobileUserToMapper;

	@Autowired
	private OTPLoginV1Mapper otpLoginV1Mapper;

	@Autowired
	private UserService userService;

	@Autowired
	private JWETokenHandler jweTokenHandler;

	@Autowired
	private UserTokenRepository tokenRepo;

	@Autowired
	private UserTypeDAO userTypeDAO;

	@Override
	public AppUserTOV1 login(final UserLoginRequestV1 mobileUserLoginRequest, final String sessionId) {
		log.info("LoginV1 called {}", mobileUserLoginRequest);

		final var userLoginRequest = loginRequestMapper.toEntity(mobileUserLoginRequest);
		userLoginRequest.setGenerateRefreshToken(true);
		final var appUserTO = authenticationService.authenticateUsernamePassword(userLoginRequest, sessionId, true);
		log.debug("Login is successful for the request {}", mobileUserLoginRequest);
		return appUserToMobileUserToMapper.toEntity(appUserTO);
	}

	@Override
	@Transactional
	public SimpleResponse logOut(final String sessionId, final String accessToken,
			final UserLogoutRequestV1 logoutRequest, final Long userId) {
		log.info("LogoutV1 called {}", logoutRequest);
		final var response = userService.logOut(StringUtils.getIfBlank(logoutRequest.getDeviceId(), () -> sessionId),
				userId, accessToken);
		if (Status.SUCCESS == response.getStatus()) {
			final var tokenData = jweTokenHandler.decodeToken(accessToken);
			if (MapUtils.isNotEmpty(tokenData) && StringUtils.isNotBlank(tokenData.get(Constants.REFRESH_TOKEN_KEY))) {
				final var userTypeTO = userTypeDAO.findByUserIdAndType(userId,
						AuthUserType.getAuthUserType(tokenData.get(Constants.USER_TYPE_KEY)));
				tokenRepo.deleteExpiredUserToken(Long.valueOf(tokenData.get(Constants.REFRESH_TOKEN_KEY)),
						userTypeTO.getUserTypeId());
			}
		}
		return response;
	}

	@Override
	public AppUserTO loginWithPhone(final OTPPhoneVerificationRequestV1 requestData, final String sessionId) {
		requestData.setGenerateRefreshToken(true);
		return authenticationService.loginWithPhone(otpLoginV1Mapper.toVO(requestData), sessionId);
	}

}
