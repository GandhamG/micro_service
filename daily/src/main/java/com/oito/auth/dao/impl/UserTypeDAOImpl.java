package com.oito.auth.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.dao.UserTypeDAO;
import com.oito.auth.dao.repository.UserTypeRepository;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.UserTypeMapper;

@Component
public class UserTypeDAOImpl implements UserTypeDAO {

	@Autowired
	private UserTypeRepository repository;

	@Autowired
	private UserTypeMapper mapper;

	@Override
	public UserTypeTO findById(final Long id) {
		return mapper
				.toVO(repository.findById(id).orElseThrow(() -> new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND)));

	}

	@Override
	public UserTypeTO save(final UserTypeTO userType) {
		return mapper.toVO(repository.save(mapper.toEntity(userType)));
	}

	@Override
	public int deleteById(final Long id) {
		return repository.deleteUserTypeById(id);
	}

	@Override
	public UserTypeTO findByUserIdAndType(final Long userId, final AuthUserType userType) {
		return mapper.toVO(repository.findByUserIdAndType(userId, userType)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND)));
	}
}
