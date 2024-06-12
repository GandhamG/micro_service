/**
 *
 */
package com.oito.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.UserCustomField;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserMetadataTO;
import com.oito.auth.common.to.UserMetadataUpdateRequest;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.dao.UserMetadataDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserMetadataService;
import com.oito.auth.validator.UserValidator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserMetadataServiceImpl implements UserMetadataService {

	@Autowired
	private UserMetadataDAO metadataDao;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserDAO userDao;

	@Override
	public List<AppUserTO> filterUserByMetadata(final String code, final String value) {
		return userDao.filterUserByMetadata(code, value);
	}

	private List<UserMetadataTO> saveAll(final List<UserMetadataTO> userMetadataTOList) {
		return metadataDao.saveAll(userMetadataTOList);
	}

	private List<UserMetadataTO> getByUserId(final Long userId) {
		return metadataDao.getByUserId(userId);
	}

	@Override
	public Map<String, String> findUserMetadataMap(final Long userId) {
		final var metadata = getByUserId(userId);
		return metadata.stream().collect(Collectors.toMap(UserMetadataTO::getCode, UserMetadataTO::getValue));
	}

	@Override
	public List<UserMetadataTO> save(final Map<String, String> customFields, final Long userId) {
		if (null != customFields) {
			final var userMetadataTOList = toUserMetadataTO(customFields, userId);
			return saveAll(userMetadataTOList);
		}
		return List.of();
	}

	private List<UserMetadataTO> toUserMetadataTO(final Map<String, String> customFields, final Long userId) {
		return customFields.entrySet().stream().map(entry -> {
			final var metadata = new UserMetadataTO();
			metadata.setUserId(userId);
			metadata.setCode(entry.getKey());
			metadata.setValue(entry.getValue());
			return metadata;
		}).collect(Collectors.toList());
	}

	@Override
	public void merge(final Map<String, String> customFields, final Long userId) {
		userValidator.validateAdminUpate(customFields);
		if (null != customFields && !customFields.isEmpty()) {
			final var dbEntries = getByUserId(userId);
			final List<UserMetadataTO> dbList = new ArrayList<>();
			dbEntries.stream().filter(userMetaDataTo -> customFields.containsKey(userMetaDataTo.getCode()))
					.forEach(userMetaDataTo -> {
						final var value = customFields.remove(userMetaDataTo.getCode());
						if (!value.equals(userMetaDataTo.getValue())) {
							userMetaDataTo.setValue(value);
							dbList.add(userMetaDataTo);
						}
					});
			dbList.addAll(toUserMetadataTO(customFields, userId));
			saveAll(dbList);
		}
	}

	@Override
	public boolean isAdmin(final Map<String, String> customFields) {
		return Boolean.parseBoolean(MapUtils.emptyIfNull(customFields).getOrDefault(UserCustomField.IS_ADMIN.getName(),
				Boolean.FALSE.toString()));
	}

	@Override
	public int countByCodeAndValue(final String code, final String value) {
		return metadataDao.countByCodeAndValue(code, value);
	}

	@Override
	@Transactional
	public void deleteByCodeAndUserId(final Set<String> codes, final Long userId) {
		metadataDao.deleteByCodeAndUserId(codes, userId);
	}

	@Override
	@Transactional
	public void updateByCode(final UserMetadataUpdateRequest request) {
		if (!request.getCode().isUpdateByCode()) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
		userContextService.verifyAdminAccess();
		userContextService.verifyPrivilege(Constants.EDIT_USER_FULL_ACCESS);
		log.info("update by code triggered {}", request);
		final var userMetadataList = metadataDao.findByCodeAndValue(request.getFilterCode().getName(),
				request.getFilterValue());
		request.setUserIds(userMetadataList.stream().map(UserMetadataTO::getUserId).collect(Collectors.toSet()));
		metadataDao.updateByCode(request);
	}

	private boolean isFirstTimeAdmin() {
		return countByCodeAndValue(UserCustomField.IS_ADMIN.getName(), Boolean.TRUE.toString()) == 0;
	}

	@Override
	public void validateAdminCreation(final Map<String, String> customFields) {
		if (isAdmin(customFields) && !isFirstTimeAdmin()) {
			userValidator.validateEditAdminPrivilege();
		}
	}

}
