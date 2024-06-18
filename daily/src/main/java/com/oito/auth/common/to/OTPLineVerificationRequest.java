package com.oito.auth.common.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;

@Getter
public class OTPLineVerificationRequest {

	@NotNull(message = "OTP Id is mandatory")
	private Long otpId;
	@NotBlank(message = "Phone number is mandatory")
	private String phoneNo;
	@NotBlank(message = "OTP Code is mandatory")
	private String otp;
	private String idToken;
	private AuthUserType userType;

}
