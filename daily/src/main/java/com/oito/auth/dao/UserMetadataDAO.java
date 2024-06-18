package com.oito.auth.dao;

import java.util.List;
import java.util.Set;

import com.oito.auth.common.to.UserMetadataTO;
import com.oito.auth.common.to.UserMetadataUpdateRequest;

public interface UserMetadataDAO {

	UserMetadataTO save(UserMetadataTO userMetadataTO);

	List<UserMetadataTO> saveAll(List<UserMetadataTO> userMetadataTOList);

	List<UserMetadataTO> getByUserId(Long userId);

	int countByUserId(Long userId);

	int countByCodeAndValue(String code, String value);

	void deleteByCodeAndUserId(final Set<String> codes, Long userId);

	void updateByCode(UserMetadataUpdateRequest request);

	List<UserMetadataTO> findByCodeAndValue(String filterCode, String filterValue);

}
