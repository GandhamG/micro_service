package com.oito.auth.service;

import java.util.List;

import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.PrivilegeRequest;

public interface PrivilegeService {
	List<PrivilegeDTO> getPrivilegeList(List<PrivilegeRequest> privilegeRequestList);

	List<String> getUserPrivileges(Long userId);
}
