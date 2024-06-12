package com.oito.auth.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.common.to.PrivilegeRequest;
import com.oito.auth.dao.PrivilegeDAO;
import com.oito.auth.dao.repository.PrivilegeRepository;
import com.oito.auth.data.Privilege;
import com.oito.auth.mapper.PrivilegeMapper;

@Component
public class PrivilegeDAOImpl implements PrivilegeDAO {

	@Autowired
	private PrivilegeMapper mapper;

	private static final String PRIVILEGE_BASE_QUERY = "select * from privilege where (resource_code, access_code) in ";

	@Autowired
	private PrivilegeRepository repo;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	/**
	 * In clause with multiple columns are not supported by JPA as of now. Due to
	 * this we're going with the custom query approach
	 */
	@Override
	public List<PrivilegeDTO> getPrivilegeList(final List<PrivilegeRequest> privilegeRequestList) {
		/*
		 * Reviewed multiple approaches for initializing entity manager and identified
		 * we can initialize for each call.
		 * https://stackoverflow.com/questions/4066048/jpa-2-using-the-entitymanager-in-
		 * javase-a-couple-of-questions
		 */
		final var entityManager = entityManagerFactory.createEntityManager();
		final var query = buildGetPrivilegesQuery(privilegeRequestList);
		final List<Privilege> privilegeList = entityManager.createNativeQuery(query, Privilege.class).getResultList();
		return mapper.toVOList(privilegeList);
	}

	@Override
	public List<PrivilegeDTO> getUserPrivilegeDTOList(final Long userId) {
		final var privilegeList = getUserPrivilegeList(userId);
		return mapper.toVOList(privilegeList);
	}

	@Override
	public List<String> getUserPrivileges(final Long userId) {
		final var privilegeList = getUserPrivilegeList(userId);
		return privilegeList.stream()
				.map(privilege -> String.join(":", privilege.getResourceCode(), privilege.getAccessCode()))
				.collect(Collectors.toList());
	}

	private String buildGetPrivilegesQuery(final List<PrivilegeRequest> privilegeRequestList) {
		final var strBuilder = new StringBuilder(PRIVILEGE_BASE_QUERY);
		strBuilder.append("(");
		for (var i = 0; i < privilegeRequestList.size(); i++) {
			if (i > 0) {
				strBuilder.append(",");
			}
			final var request = privilegeRequestList.get(i);
			strBuilder.append("('");
			strBuilder.append(request.getResourceCode());
			strBuilder.append("','");
			strBuilder.append(request.getAccessCode());
			strBuilder.append("')");

		}
		strBuilder.append(")");
		return strBuilder.toString();
	}

	private List<Privilege> getUserPrivilegeList(final Long userId) {
		return repo.getUserPrivilegeList(userId);
	}
}
