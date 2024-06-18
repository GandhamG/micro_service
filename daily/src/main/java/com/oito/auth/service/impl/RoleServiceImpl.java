package com.oito.auth.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.RoleRequest;
import com.oito.auth.dao.RoleDAO;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.RoleService;
import com.oito.auth.service.UserPrivilegeAssignmentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDAO roleDAO;

	@Autowired
	private UserDAO userDao;

	@Autowired
	private UserPrivilegeAssignmentService assignmentService;

	@Override
	public List<RoleDTO> getRoleList(final List<String> roleCodeList) {
		if (CollectionUtils.isEmpty(roleCodeList)) {
			return Collections.emptyList();
		}
		final var roleList = roleDAO.getByRoleCodeList(roleCodeList);
		final List<String> roleCodeListCopy = new ArrayList<>(roleCodeList);
		for (final RoleDTO role : roleList) {
			roleCodeListCopy.remove(role.getRoleCode());
		}
		if (!roleCodeListCopy.isEmpty()) {
			log.error("Invalid roles found {}", roleCodeListCopy);
			throw new AuthException(AuthErrorCode.INVALID_ROLE);
		}
		return roleList;
	}

	public List<RoleDTO> getRoleListById(final List<Long> roleIdList) {
		if (CollectionUtils.isEmpty(roleIdList)) {
			return Collections.emptyList();
		}
		final var roleList = roleDAO.getByRolesByIds(roleIdList);
		final List<Long> roleIdListCopy = new ArrayList<>(roleIdList);
		for (final RoleDTO role : roleList) {
			roleIdListCopy.remove(role.getRoleId());
		}
		if (!roleIdListCopy.isEmpty()) {
			log.error("Invalid roles found {}", roleIdListCopy);
			throw new AuthException(AuthErrorCode.INVALID_ROLE);
		}
		return roleList;
	}

	@Override
	public List<RoleDTO> getRoles() {
		return roleDAO.getRoles();
	}

	@Override
	public List<RoleDTO> getUserRoles(final Long userId) {
		return roleDAO.getUserRoles(userId);
	}

	private AppUserTO getUserById(final Long userId) {
		return userDao.findById(userId).orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
	}

	@Override
	@Transactional
	public void deleteUserRoles(final RoleRequest request) {
		log.info("Delete Role Request {}", request);
		roleDAO.deleteUserRoles(request);
	}

	@Override
	@Transactional
	public List<RoleDTO> addUserRoles(final RoleRequest request) {
		final var user = getUserById(request.getUserId());
		List<RoleDTO> roleList;
		if (CollectionUtils.isNotEmpty(request.getRoleIds())) {
			roleList = getRoleListById(request.getRoleIds());
		} else {
			roleList = getRoleList(request.getRoleCodes());
		}
		assignmentService.save(user, roleList, Collections.emptyList());
		return roleList;
	}

}
