package com.oito.auth.dao;

import java.util.List;

import com.oito.auth.common.to.UserPrivilegeAssignmentDTO;

public interface UserPrivilegeAssignmentDAO {

	void save(List<UserPrivilegeAssignmentDTO> assignmentList);

}
