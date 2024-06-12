package com.oito.auth.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.oito.auth.common.to.UserDeleteRequestDTO;
import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.common.to.UserDeleteResponse;

public interface UserDeleteRequestDAO {

	void save(final UserDeleteRequestDTO userDeleteRequest);

	void update(final UserDeleteRequestDTO userDeleteRequest);

	Optional<UserDeleteRequestDTO> findByUserId(final Long userId);

	void updateUserDeleteRequest(UserDeleteRequestDTO dto);

	Optional<UserDeleteRequestDTO> findByUserIdAndStatus(Long userId, UserDeleteRequestStatus status);

	Long countByUserIdAndStatus(Long userId, UserDeleteRequestStatus status);

	List<UserDeleteResponse> findByStatus(final UserDeleteRequestStatus status, Long interval);

	Map<Long, UserDeleteRequestDTO> findByUserIds(final List<Long> userIds);

	void updateUserDeleteRequest(List<Long> userIds, UserDeleteRequestStatus status);

}
