package com.oito.auth.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oito.auth.common.UserPrivilegeAssignmentType;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.UserPrivilegeAssignmentDTO;
import com.oito.auth.dao.UserPrivilegeAssignmentDAO;
import com.oito.auth.service.UserPrivilegeAssignmentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserPrivilegeAssignmentServiceImpl implements UserPrivilegeAssignmentService {

	@Autowired
	private UserPrivilegeAssignmentDAO assignmentDAO;

	@Override
	public void save(final AppUserTO user, final List<RoleDTO> roleList, final List<PrivilegeDTO> privilegeList) {
		final List<UserPrivilegeAssignmentDTO> assignmentList = new ArrayList<>();
		final var userId = user.getUserId();
		roleList.forEach(role -> assignmentList
				.add(new UserPrivilegeAssignmentDTO(userId, UserPrivilegeAssignmentType.ROLE, role.getRoleId())));
		privilegeList.forEach(privilege -> assignmentList.add(new UserPrivilegeAssignmentDTO(userId,
				UserPrivilegeAssignmentType.PRIVILEGE, privilege.getPrivilegeId())));
		if (CollectionUtils.isNotEmpty(assignmentList)) {
			assignmentDAO.save(assignmentList);
			log.info("Successfully saved the role and privilege assignment record list");
		}
	}

}
