package com.oito.auth.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.PrivilegeRequest;
import com.oito.auth.dao.PrivilegeDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.PrivilegeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrivilegeServiceImpl implements PrivilegeService {

	@Autowired
	private PrivilegeDAO privilegeDAO;

	@Override
	public List<PrivilegeDTO> getPrivilegeList(final List<PrivilegeRequest> privilegeRequestList) {
		if (CollectionUtils.isEmpty(privilegeRequestList)) {
			return Collections.emptyList();
		}
		final var privilegeList = privilegeDAO.getPrivilegeList(privilegeRequestList);
		final var requestIterator = privilegeRequestList.iterator();
		while (requestIterator.hasNext()) {
			final var request = requestIterator.next();
			final var resultCount = privilegeList.stream()
					.filter(privilege -> privilege.getAccessCode().equals(request.getAccessCode())
							&& privilege.getResourceCode().equals(request.getResourceCode()))
					.count();
			if (resultCount == 0) {
				log.error("No privilege exists for {}", request);
				throw new AuthException(AuthErrorCode.INVALID_PRIVILEGE_REQUEST);
			}
		}
		return privilegeList;

	}

	@Override
	public List<String> getUserPrivileges(final Long userId) {
		return privilegeDAO.getUserPrivileges(userId);
	}
}
