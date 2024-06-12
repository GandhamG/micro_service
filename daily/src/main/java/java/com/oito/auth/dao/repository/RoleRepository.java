/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.data.Role;

/**
 *
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

	List<Role> findByRoleCodeIn(List<String> roleCodeList);

	@Query("SELECT r from Role r join UserPrivilegeAssignment u on r.roleId=u.typeId and u.type='ROLE' where u.userId=:userId")
	List<Role> findUserRoles(@Param("userId") Long userId);

	@Modifying
	@Query("DELETE from UserPrivilegeAssignment u where u.userId=:userId and u.type='ROLE' and u.typeId in :typeIds")
	void deleteUserRoles(@Param("userId") Long userId, @Param("typeIds") List<Long> typeIds);

}
