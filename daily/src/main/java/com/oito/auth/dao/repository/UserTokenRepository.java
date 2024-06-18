/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.common.UserTokenType;
import com.oito.auth.data.UserToken;

/**
 * @author Dileep
 *
 */
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

	Optional<UserToken> findByToken(String token);

	UserToken findByTokenTypeAndMacIdAndUserTypeId(UserTokenType tokenType, String macId, Long userTypeId);

	@Modifying
	@Query("DELETE FROM UserToken ut WHERE  ut.userTokenId = :tokenId OR (ut.userTypeId = :userTypeId AND ut.expiryTimeStamp < CURRENT_TIMESTAMP)")
	void deleteExpiredUserToken(@Param("tokenId") final Long tokenId, @Param("userTypeId") Long userTypeId);

}
