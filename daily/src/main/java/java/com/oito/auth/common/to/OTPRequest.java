package com.oito.auth.common.to;

import java.util.Map;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OTPRequest {

	private Long otpId;
	private Long userId;
	private String locale;
	private String phone;
	private String email;
	private String otpTemplate = "otp.message";
	private String emailTemplate = "otp";
	private String emailSubject;
	private Map<String, Object> userParams;
	private AuthUserType userType;

	public OTPRequest(final String phone, final String locale) {
		this.phone = phone;
		this.locale = locale;
	}
}
