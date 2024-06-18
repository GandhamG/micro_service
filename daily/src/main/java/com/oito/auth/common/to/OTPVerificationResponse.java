package com.oito.auth.common.to;

import lombok.Getter;

@Getter
public class OTPVerificationResponse extends OTPResponse {

	private final boolean verified;

	public OTPVerificationResponse(final Status status, final Long otpId, final boolean verified) {
		super(status, otpId);
		this.verified = verified;
	}

}
