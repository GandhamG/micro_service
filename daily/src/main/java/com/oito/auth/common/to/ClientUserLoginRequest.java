package com.oito.auth.common.to;

import javax.validation.constraints.NotNull;

import com.oito.auth.common.AuthUserType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ClientUserLoginRequest {

	@ApiModelProperty(notes = "UserName of the Client", example = "zdsdfswwe123DFfewe", required = true)
	@NotNull
	private String userName;

	@ApiModelProperty(notes = "Password of the Client", example = "zdsdfswwe123DFfewe", required = true)
	private String password;

	@ApiModelProperty(notes = "UserType of the Client", example = "DAM", required = true)
	private AuthUserType userType;

}
