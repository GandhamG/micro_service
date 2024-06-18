/**
 *
 */
package com.oito.auth.v1.common.to;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oito.auth.common.ErrorAction;
import com.oito.auth.common.to.Status;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.json.UserTypeTOSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "UserSignUpResponseV1", description = "This represents the user signup response version1")
public class SignUpResponseV1 {

	@ApiModelProperty(notes = "Status", example = "FAILURE", allowableValues = "SUCCESS, FAILURE")
	private Status status;

	@ApiModelProperty(notes = "Verification Token", example = "N3UrrRCnskyCBKLKmSelL7NcZD")
	private String verificationToken;

	@ApiModelProperty(notes = "User Id", example = "5317")
	private Long userId;

	@ApiModelProperty(notes = "User Email", example = "[{\"userTypeId\":\"01\", \"userId\":\"5317\", \"type\":\"BUYER\", \"errorMessage\":\"Error Message1\", \"errorCode\": \"ADD_USER_TYPE_FAILURE\", \"enabled\": true }]")
	@JsonSerialize(using = UserTypeTOSerializer.class)
	private Set<UserTypeTO> userTypes;

	@ApiModelProperty(notes = "Error", example = "Failed to add new user type")
	private String error;

	@ApiModelProperty(notes = "User Email", example = "ADD_USER_TYPE_FAILURE")
	private AuthErrorCode errorCode;

	@ApiModelProperty(notes = "Masked User Email", example = "u******n@domain.com")
	private String maskedUserEmail;

	@ApiModelProperty(notes = "Error Action", example = "GET_PASSWORD", allowableValues = "SHOW_ERROR, GET_EMAIL_PASSWORD, GET_PASSWORD, WARNING")
	private ErrorAction errorAction;

	@ApiModelProperty(notes = "Warning", example = "warning message1")
	private String warning;

	@ApiModelProperty(notes = "Warning Code", example = "W001")
	private String warningCode;

}
