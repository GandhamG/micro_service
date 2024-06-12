package com.oito.auth.dao;

import java.util.List;

import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.RoleRequest;

public interface RoleDAO {
	List<RoleDTO> getByRoleCodeList(List<String> roleCodeList);

	List<RoleDTO> getByRolesByIds(List<Long> roleIds);

	List<RoleDTO> getRoles();

	List<RoleDTO> getUserRoles(Long userId);

	void deleteUserRoles(RoleRequest request);

}
