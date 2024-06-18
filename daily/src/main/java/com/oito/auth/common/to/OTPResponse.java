package com.oito.auth.common.to;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oito.auth.json.UserTypeTOSerializer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class OTPResponse extends SimpleResponse {

	private Long otpId;
	private String maskedPhone;

	@JsonSerialize(using = UserTypeTOSerializer.class)
	private Set<UserTypeTO> userTypes;

	public OTPResponse(final Status status, final Long otpId) {
		super(status);
		this.otpId = otpId;
	}
}
