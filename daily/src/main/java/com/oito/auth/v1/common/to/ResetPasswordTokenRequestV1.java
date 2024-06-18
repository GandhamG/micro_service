package com.oito.auth.v1.common.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(value = "ResetPasswordTokenRequestV1", description = "This represents the create token request version1, One of the below fields are mandatory")
public class ResetPasswordTokenRequestV1 {

	@ApiModelProperty(notes = "User Email", example = "ajay@scg.com")
	private String useremail;

	@ApiModelProperty(notes = "User Name", example = "Ajay")
	private String userName;

	@ApiModelProperty(notes = "Phone Number", example = "+919989470491")
	private String phoneNo;

}
