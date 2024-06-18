package com.oito.auth.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.RoleRequest;
import com.oito.auth.dao.RoleDAO;
import com.oito.auth.dao.repository.RoleRepository;
import com.oito.auth.mapper.RoleMapper;

@Component
public class RoleDAOImpl implements RoleDAO {

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private RoleMapper mapper;

	@Override
	public List<RoleDTO> getByRoleCodeList(final List<String> roleCodeList) {
		return mapper.toVOList(roleRepo.findByRoleCodeIn(roleCodeList));
	}

	@Override
	public List<RoleDTO> getRoles() {
		return mapper.toVOList(roleRepo.findAll());
	}

	@Override
	public List<RoleDTO> getUserRoles(final Long userId) {
		return mapper.toVOList(roleRepo.findUserRoles(userId));
	}

	@Override
	public void deleteUserRoles(final RoleRequest request) {
		roleRepo.deleteUserRoles(request.getUserId(), request.getRoleIds());
	}

	@Override
	public List<RoleDTO> getByRolesByIds(final List<Long> roleIds) {
		return mapper.toVOList(roleRepo.findAllById(roleIds));
	}

}
