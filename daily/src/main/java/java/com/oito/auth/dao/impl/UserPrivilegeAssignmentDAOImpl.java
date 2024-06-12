package com.oito.auth.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.to.UserPrivilegeAssignmentDTO;
import com.oito.auth.dao.UserPrivilegeAssignmentDAO;
import com.oito.auth.dao.repository.UserPrivilegeAssignmentRepository;
import com.oito.auth.mapper.UserPrivilegeAssignmentMapper;

@Component
public class UserPrivilegeAssignmentDAOImpl implements UserPrivilegeAssignmentDAO {
	@Autowired
	private UserPrivilegeAssignmentRepository repo;

	@Autowired
	private UserPrivilegeAssignmentMapper mapper;

	@Override
	public void save(final List<UserPrivilegeAssignmentDTO> assignmentList) {
		repo.saveAll(mapper.toEntityList(assignmentList));
	}
}
