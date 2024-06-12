package com.oito.auth.common.to;

import lombok.Getter;

@Getter
public class UserTypeResponse extends SimpleResponse {

	private AppUserTO user;

	public UserTypeResponse(final Status status, final AppUserTO user) {
		super(status);
		this.user = user;
	}

}
