package com.oito.auth.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.UserTokenType;
import com.oito.auth.common.to.UserTokenTO;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.dao.UserTokenDAO;
import com.oito.auth.dao.UserTypeDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserTokenServiceImpl implements UserTokenService {

	@Autowired
	private UserTokenDAO userTokenDAO;

	@Autowired
	private UserTypeDAO userTypeDAO;

	@Value("${oito.token.refresh-token-expiry-hours:48}")
	private int refreshTokenExpiryHours;

	private LocalDateTime getExpiryTimeStamp() {
		return LocalDateTime.now().plus(Duration.of(refreshTokenExpiryHours, ChronoUnit.HOURS));
	}

	public String generateRefreshToken() {
		final var refreshToken = new RefreshToken();
		return refreshToken.getValue();
	}

	@Override
	@Transactional
	public UserTokenTO getValidRefreshToken(final String token) {
		final var userTokenTO = userTokenDAO.findByToken(token);
		// Checking if its expired
		if (LocalDateTime.now().isAfter(userTokenTO.getExpiryTimeStamp())) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
		setTokenFields(userTokenTO);
		userTokenDAO.save(userTokenTO);
		return userTokenTO;
	}

	private void setTokenFields(final UserTokenTO userTokenTO) {
		userTokenTO.setExpiryTimeStamp(getExpiryTimeStamp());
		userTokenTO.setToken(generateRefreshToken());
	}

	@Override
	public UserTokenTO postLoginRefreshToken(final String macId, final Long userId, final AuthUserType userType) {
		final var userTypeTO = userTypeDAO.findByUserIdAndType(userId, userType);
		final var userTokenTO = userTokenDAO.findUserTokenByTypeAndMacIdAndUserTypeId(UserTokenType.REFRESH, macId,
				userTypeTO.getUserTypeId());
		return userTokenDAO.save(populateUserTokenVO(macId, userTypeTO, userTokenTO));
	}

	private UserTokenTO populateUserTokenVO(final String macId, final UserTypeTO userTypeTO,
			final UserTokenTO userToken) {
		var tokenVo = userToken;
		if (tokenVo == null) {
			tokenVo = new UserTokenTO();
			tokenVo.setMacId(macId);
			tokenVo.setTokenType(UserTokenType.REFRESH);
			tokenVo.setUserTypeId(userTypeTO.getUserTypeId());
		}
		setTokenFields(tokenVo);
		return tokenVo;
	}

}
