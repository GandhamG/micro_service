package com.oito.auth.common;

import com.google.common.base.Enums;

public enum AuthUserType {
	BUYER, SELLER, PROFESSIONAL, DAM;

	public static AuthUserType getAuthUserType(final String type) {
		return Enums.getIfPresent(AuthUserType.class, type).orNull();
	}
}
