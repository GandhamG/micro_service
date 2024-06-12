/**
 *
 */
package com.oito.auth.service;

import java.util.List;

import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.RoleRequest;

/**
 * Basic User Services interface
 *
 * @author Jobin John
 *
 */
public interface RoleService {

	List<RoleDTO> getRoleList(List<String> roleCodeList);

	List<RoleDTO> getRoles();

	void deleteUserRoles(RoleRequest request);

	List<RoleDTO> addUserRoles(RoleRequest request);

	List<RoleDTO> getUserRoles(Long userId);

}
