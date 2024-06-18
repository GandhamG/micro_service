package com.oito.auth.v1.common.to;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "OTPPhoneVerificationRequestV1", description = "This represents the otp Login request")
public class OTPPhoneVerificationRequestV1 {

	@NotNull(message = "OTP Id is mandatory")
	@ApiModelProperty(notes = "Otp Id", example = "1234", required = true)
	private Long otpId;

	@NotBlank(message = "Phone number is mandatory")
	@ApiModelProperty(notes = "Phone Number", example = "+66123456789", required = true)
	private String phoneNo;

	@NotBlank(message = "OTP Code is mandatory")
	@ApiModelProperty(notes = "Otp Received in phone", example = "523456", required = true)
	private String otp;

	@ApiModelProperty(notes = "User Type", example = "BUYER", required = true, allowableValues = "BUYER")
	@NotNull(message = "User type is mandatory")
	private AuthUserType userType;

	@ApiModelProperty(notes = "Stay signed in flag", example = "false", allowableValues = "true,false")
	private Boolean staySignedIn;

	@JsonIgnore
	private boolean generateRefreshToken;

	@ApiModelProperty(notes = "Unique Machine Id", example = "asdfx")
	private String macId;

}
