/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.data.UserLoginHistory;

/**
 * @author Jobin John
 *
 */
public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {

	Optional<UserLoginHistory> findByUserIdAndSessionId(Long userId, String sessionId);

	@Modifying
	@Query("update UserLoginHistory u set u.logoutTimestamp=CURRENT_TIMESTAMP where u.userId = :userId and u.sessionId=:sessionId")
	int updateByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

}
