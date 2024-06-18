package com.oito.auth.dao;

import java.util.List;

import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.PrivilegeRequest;

public interface PrivilegeDAO {

	List<PrivilegeDTO> getPrivilegeList(List<PrivilegeRequest> privilegeRequestList);

	List<PrivilegeDTO> getUserPrivilegeDTOList(Long userId);

	List<String> getUserPrivileges(Long userId);

}
