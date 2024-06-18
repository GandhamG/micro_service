/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.data.UserType;

/**
 * @author Dileep
 *
 */
public interface UserTypeRepository extends JpaRepository<UserType, Long> {

	@Modifying
	@Query("delete from UserType where userTypeId=:userTypeId")
	int deleteUserTypeById(@Param("userTypeId") Long userTypeId);

	Optional<UserType> findByUserIdAndType(Long userId, AuthUserType userType);
}
