package com.oito.auth.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.UserTokenType;
import com.oito.auth.common.to.UserTokenTO;
import com.oito.auth.dao.UserTokenDAO;
import com.oito.auth.dao.repository.UserTokenRepository;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.UserTokenMapper;

@Component
public class UserTokenDAOImpl implements UserTokenDAO {

	@Autowired
	private UserTokenRepository repo;

	@Autowired
	private UserTokenMapper userTokenMapper;

	@Override
	public UserTokenTO save(final UserTokenTO userTokenTO) {
		final var userToken = repo.save(userTokenMapper.toEntity(userTokenTO));
		return userTokenMapper.toVO(userToken);
	}

	@Override
	public UserTokenTO findByToken(final String token) throws AuthException {
		return userTokenMapper.toVO(
				repo.findByToken(token).orElseThrow(() -> new AuthException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND)));

	}

	@Override
	public UserTokenTO findUserTokenByTypeAndMacIdAndUserTypeId(final UserTokenType tokenType, final String macId,
			final Long userTypeId) {
		return userTokenMapper.toVO(repo.findByTokenTypeAndMacIdAndUserTypeId(tokenType, macId, userTypeId));
	}

}
