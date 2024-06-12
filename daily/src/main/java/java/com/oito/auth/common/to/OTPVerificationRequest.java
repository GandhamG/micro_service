package com.oito.auth.common.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPVerificationRequest {
	@NotNull(message = "OTP Id is mandatory")
	private Long otpId;
	@NotBlank(message = "OTP Code is mandatory")
	private String otp;
}
