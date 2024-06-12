/**
 *
 */
package com.oito.auth.dao.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.data.UserDeleteRequest;

/**
 * @author Dileep
 *
 */
public interface UserDeleteRequestRepository extends JpaRepository<UserDeleteRequest, Long> {

	Optional<UserDeleteRequest> findByUserId(Long userId);

	List<UserDeleteRequest> findByUserIdIn(List<Long> userIds);

	Optional<UserDeleteRequest> findByUserIdAndStatus(Long userId, UserDeleteRequestStatus status);

	List<UserDeleteRequest> findByStatus(UserDeleteRequestStatus status);

	Long countByUserIdAndStatus(Long userId, UserDeleteRequestStatus status);

	@Query("select userId from UserDeleteRequest userDeleteRequest where userDeleteRequest.status=:status and userDeleteRequest.processedTimestamp <= :date")
	List<Long> findOpenDeleteRequestInterval(@Param("date") LocalDateTime date,
			@Param("status") UserDeleteRequestStatus status);

	@Modifying
	@Query("update UserDeleteRequest u set u.status=:status, u.processedTimestamp=CURRENT_TIMESTAMP where u.userId = :userId")
	void updateUserDeleteRequest(Long userId, UserDeleteRequestStatus status);

	@Modifying
	@Query("update UserDeleteRequest u set u.status=:status, u.processedTimestamp=CURRENT_TIMESTAMP where u.userId IN  :userIds")
	void updateUserDeleteRequest(@Param("userIds") List<Long> userIds, @Param("status") UserDeleteRequestStatus status);

}
