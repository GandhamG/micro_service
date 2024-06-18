package com.oito.auth.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.oito.auth.common.enumeration.UserCustomField;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.common.usercontext.UserContext;
import com.oito.common.usercontext.UserContextStore;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserContextService {

	private static final Long DEFAULT_USER = Long.valueOf(-90L);

	@Autowired
	private UserContextStore userContextStore;

	/**
	 * Logged in user context
	 *
	 * @return
	 */
	public UserContext getUserContext() {
		return userContextStore.getUserContext();
	}

	/**
	 * Logged in user id
	 *
	 * @return
	 */
	public Long getUserId() {
		final var userContext = getUserContext();
		if (null != userContext) {
			return userContext.getUserId();
		}
		log.error("User context not found");
		throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
	}

	public Long getUserIdNullSafe() {
		final var userContext = getUserContext();
		if (userContext == null || userContext.getUserId() == null) {
			return DEFAULT_USER;
		}
		return userContext.getUserId();
	}

	/**
	 * Logged in user email
	 *
	 * @return
	 */
	public String getUserEmail() {
		final var userContext = getUserContext();
		if (null != userContext) {
			return userContext.getUserEmail();
		}
		log.error("User context not found");
		return null;
	}

	/**
	 * Any custom field set in Logged in user context
	 *
	 * @param key
	 * @return
	 */
	public String getCustomField(final String key) {
		final var userContext = getUserContext();
		log.info("user context from thread {}", userContext);
		if (null != userContext) {
			log.info("Custom fields {}", userContext.getCustomFields());
			return userContext.getCustomField(key);
		}
		log.error("User context doesn't have the custom field " + key);
		return null;
	}

	public String getCustomField(final UserCustomField customField) {
		return getCustomField(customField.getName());
	}

	/**
	 * Logged in user's seller id
	 *
	 * @return
	 */
	public Long getSupplierId() {
		final var supplierId = getCustomField(UserCustomField.SUPPLIER_ID);
		if (null != supplierId) {
			return Long.valueOf(supplierId);
		}
		log.error("User context doesn't have the supplier Id");
		return null;
	}

	public List<String> getPrivileges() {
		final var userContext = getUserContext();
		if (null != userContext && userContext.getPrivilegeList() != null) {
			return userContext.getPrivilegeList();
		}
		log.error("User context doesn't have privileges");
		return Collections.emptyList();
	}

	/**
	 * Checks whether the user is logged in as an admin
	 *
	 * @return
	 */
	public boolean isAdminLogin() {
		final var isAdminObj = getCustomField(UserCustomField.IS_ADMIN);
		return null != isAdminObj && Boolean.parseBoolean(isAdminObj);
	}

	public void verifyUserResourceAccess(final Long userId) {
		if (!isAdminOrLoggedInUser(userId)) {
			log.info("Invalid Access User Context Received is {}", getUserContext());
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	public void verifyAdminAccess() {
		if (!isAdminLogin()) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	public void verifyUserEditAccess(final Long userId) {
		if (!hasUserEditAccess(userId)) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	public boolean hasUserEditAccess(final Long userId) {
		return getUserId().equals(userId) || (isAdminLogin() && hasPrivilege("USER_EDIT:FULL_ACCESS"));

	}

	public void verifyPrivilege(final String privilege) {
		if (!hasPrivilege(privilege)) {
			log.info("User Privilege List {}", getPrivileges());
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	public boolean hasPrivilege(final String privilege) {
		return getPrivileges().contains(privilege);
	}

	public boolean isAdminOrLoggedInUser(final Long userId) {
		return isAdminLogin() || getUserId().equals(userId);
	}

	public void verifyAdminOrLoggedInUser(final Long userId) {
		if (!isAdminOrLoggedInUser(userId)) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	public boolean isPrimaryContact() {
		return Strings.nullToEmpty(getCustomField(UserCustomField.CONTACT_ID))
				.equals(getCustomField(UserCustomField.PRIMARY_CONTACT_ID));
	}

	public void verifyPrimaryContact() {
		if (!isPrimaryContact()) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}
}
