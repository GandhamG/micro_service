package com.oito.auth.dao.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.oito.auth.common.to.UserMetadataTO;
import com.oito.auth.common.to.UserMetadataUpdateRequest;
import com.oito.auth.dao.UserMetadataDAO;
import com.oito.auth.dao.repository.UserMetadataRepository;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.UserMetadataMapper;

@Component
public class UserMetaDataDAOImpl implements UserMetadataDAO {

	@Autowired
	private UserMetadataRepository metadataRepository;

	@Autowired
	private UserMetadataMapper userMetadataMapper;

	@Override
	public UserMetadataTO save(final UserMetadataTO userMetadataTO) {
		return userMetadataMapper.toVO(metadataRepository.save(userMetadataMapper.toEntity(userMetadataTO)));
	}

	@Override
	public List<UserMetadataTO> saveAll(final List<UserMetadataTO> userMetadataTOList) {
		try {
			return userMetadataMapper
					.toVOList(metadataRepository.saveAll(userMetadataMapper.toEntityList(userMetadataTOList)));
		} catch (final DataIntegrityViolationException e) {
			throw new AuthException(AuthErrorCode.DUPLICATE_KEY, e);
		}
	}

	@Override
	public List<UserMetadataTO> getByUserId(final Long userId) {
		return userMetadataMapper.toVOList(metadataRepository.findByUserId(userId));
	}

	@Override
	public int countByUserId(final Long userId) {
		return metadataRepository.countByUserId(userId);
	}

	@Override
	public int countByCodeAndValue(final String code, final String value) {
		return metadataRepository.countByCodeAndValue(code, value);
	}

	@Override
	public void deleteByCodeAndUserId(final Set<String> codes, final Long userId) {
		metadataRepository.deleteByCodeAndUserId(codes, userId);
	}

	@Override
	public void updateByCode(final UserMetadataUpdateRequest request) {
		metadataRepository.updateByCode(request.getCode().getName(), request.getExistingValue(), request.getNewValue(),
				request.getUserIds());

	}

	@Override
	public List<UserMetadataTO> findByCodeAndValue(final String code, final String value) {
		return userMetadataMapper.toVOList(metadataRepository.findByCodeAndValue(code, value));
	}

}
