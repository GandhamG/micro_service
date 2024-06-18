package com.oito.auth.v1.common.to;

import java.util.Map;

import com.oito.auth.common.AuthUserType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
@ApiModel(value = "UserLoginRequestV1", description = "This represents the login request version1")
public class UserLoginRequestV1 {

	@ApiModelProperty(notes = "User Email", example = "useremail@domain.com", required = false)
	private String useremail;

	@ApiModelProperty(notes = "User Password", example = "password", required = true)
	private String password;

	@ApiModelProperty(notes = "User Phone Number", example = "+61123456789", required = true)
	private String phoneNo;

	@ApiModelProperty(notes = "User Type", example = "BUYER", required = true, allowableValues = "BUYER,SELLER,PROFESSIONAL")
	private AuthUserType userType;

	@ApiModelProperty(notes = "Stay signed in flag", example = "false", allowableValues = "true,false")
	private Boolean staySignedIn = Boolean.FALSE;

	// Added for userToken
	@ApiModelProperty(notes = "Unique Machine Id", example = "asdfx")
	private String macId; // TODO : standards for getting macid

	@ApiModelProperty(notes = "Token parameters to be stored as part of accessToken", example = "{\"field1\":\"value1\"}")
	private Map<String, String> tokenParams;

}
