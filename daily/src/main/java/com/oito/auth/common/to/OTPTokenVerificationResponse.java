package com.oito.auth.common.to;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPTokenVerificationResponse extends SimpleResponse {

	private String verificationToken;

	public OTPTokenVerificationResponse(final Status status, final String verificationToken) {
		super(status);
		this.verificationToken = verificationToken;
	}

}
