/**
 *
 */
package com.oito.auth.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserMetadataTO;
import com.oito.auth.common.to.UserMetadataUpdateRequest;

public interface UserMetadataService {

	List<UserMetadataTO> save(final Map<String, String> customFields, final Long userId);

	Map<String, String> findUserMetadataMap(Long userId);

	void merge(Map<String, String> customFields, Long userId);

	boolean isAdmin(Map<String, String> customFields);

	int countByCodeAndValue(String code, String value);

	void deleteByCodeAndUserId(final Set<String> codes, final Long userId);

	void updateByCode(final UserMetadataUpdateRequest userMetadataUpdateRequest);

	void validateAdminCreation(Map<String, String> customFields);

	List<AppUserTO> filterUserByMetadata(String code, String value);

}
