package com.oito.auth.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserCustomField {

	SUPPLIER_ID("supplierId", true), CONTACT_ID("contactId", true), PRIMARY_CONTACT_ID("primaryContactId", true),
	COOKIE_ENABLED("cookieEnabled"), AB_TYPE("abType"), SHARE_PERSONAL_INFO("sharePersonalInfo"),
	SUBSCRIBE_NEWS_LETTERS("subscribeNewsletters"), IS_ADMIN("isAdmin");

	UserCustomField(final String name) {
		this.name = name;
		this.updateByCode = false;
	}

	private String name;

	private boolean updateByCode;

}
