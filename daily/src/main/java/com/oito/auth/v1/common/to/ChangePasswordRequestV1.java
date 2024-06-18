package com.oito.auth.v1.common.to;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "ChangePasswordRequestV1", description = "This represents the change password request version1")
public class ChangePasswordRequestV1 {

	@NotBlank
	@ApiModelProperty(notes = "Current Password", example = "password1", required = true)
	private String currentPassword;

	@NotBlank
	@ApiModelProperty(notes = "New Password", example = "password2", required = true)
	private String newPassword;

}
