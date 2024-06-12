package com.oito.auth.service;

import java.util.List;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.RoleDTO;

public interface UserPrivilegeAssignmentService {
	void save(final AppUserTO user, List<RoleDTO> roleList, List<PrivilegeDTO> privilegeList);

}
