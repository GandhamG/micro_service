package com.oito.auth.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.data.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

	@Query(nativeQuery = true, value = "SELECT pr.* FROM user_privilege_assignment upr_as JOIN privilege pr ON upr_as.type_id = pr.privilege_id AND upr_as.type = 'PRIVILEGE' "
			+ "WHERE upr_as.user_id = :userId UNION "
			+ "SELECT PR.* FROM user_privilege_assignment upr_as JOIN role_privilege_mapping rpr_map ON rpr_map.role_id = upr_as.type_id AND upr_as.type = 'ROLE' JOIN privilege PR ON PR.privilege_id = rpr_map.privilege_id "
			+ "WHERE upr_as.user_id = :userId")
	List<Privilege> getUserPrivilegeList(@Param("userId") Long userId);

}