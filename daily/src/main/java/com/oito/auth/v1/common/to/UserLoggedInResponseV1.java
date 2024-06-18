package com.oito.auth.v1.common.to;

import java.util.Map;

import com.oito.auth.common.to.UserStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(value = "UserLoggedInResponseV1", description = "This represents the user logged in response version1")
public class UserLoggedInResponseV1 {

	@ApiModelProperty(notes = "User Id", example = "5317")
	private Long userId;

	@ApiModelProperty(notes = "User Full Name", example = "Chrish Gayle")
	private String fullName;

	@ApiModelProperty(notes = "User Email", example = "useremail@domain.com")
	private String useremail;

	@ApiModelProperty(notes = "User Phone Number", example = "+919989470491")
	private String phoneNo;

	@ApiModelProperty(notes = "Custome Fields", example = "{\"field1\":\"value1\"}")
	private Map<String, String> customFields;

	@ApiModelProperty(notes = "Country Code Of Phone Number", example = "+66,+91")
	private String phoneCountryCode;

	@ApiModelProperty(notes = "Status", example = "ACTIVE")
	private UserStatus status;
}
