/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.data.UserMetadata;

public interface UserMetadataRepository extends JpaRepository<UserMetadata, Long> {

	List<UserMetadata> findByUserId(Long userId);

	int countByUserId(Long userId);

	int countByCodeAndValue(String code, String value);

	List<UserMetadata> findByCodeAndValue(String code, String value);

	@Modifying
	@Query("DELETE from UserMetadata u where u.code in :code and u.userId=:userId")
	void deleteByCodeAndUserId(@Param("code") Set<String> code, @Param("userId") Long userId);

	@Modifying
	@Query("UPDATE UserMetadata u set u.value = :newValue where u.code = :code and u.value = :existingValue and u.userId in :userIds")
	void updateByCode(@Param("code") String code, @Param("existingValue") String existingValue,
			@Param("newValue") String newValue, @Param("userIds") Set<Long> userIds);

}
