package com.oito.auth.v1.common.to;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "ResetPasswordRequestV1", description = "This represents the reset password request version1")
public class ResetPasswordRequestV1 {

	@NotBlank
	@ApiModelProperty(notes = "Token", example = "sAEhoLqOdFZUNdwPbJx_mQCqC_WUANoVMOinpLSSeDklUjO7bCgsYRHPqu913jSptctWuAg", required = true)
	private String token;

	@NotBlank
	@ApiModelProperty(notes = "New Password", example = "password", required = true)
	private String newPassword;

}
