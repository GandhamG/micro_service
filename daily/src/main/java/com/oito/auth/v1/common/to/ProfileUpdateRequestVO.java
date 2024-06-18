package com.oito.auth.v1.common.to;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(value = "UserProfileUpdateRequestV1", description = "This represents the user profile update request version1")
public class ProfileUpdateRequestVO {

	@JsonIgnore
	@ApiModelProperty(notes = "User Id", example = "5317")
	private Long userId;

	@ApiModelProperty(notes = "User Phone Number", example = "+919989470491")
	@Pattern(regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$", message = "Enter proper phone number with country code")
	private String phoneNo;

	@ApiModelProperty(notes = "User Email", example = "sample@email.com")
	private String useremail;

	@ApiModelProperty(notes = "Full Name", example = "Gandham Ganesh")
	@Pattern(regexp = "^[^\\ ].*[^\\ .]$", message = "Full name should not be empty")
	private String fullName;

	@JsonIgnore
	@ApiModelProperty(notes = "Country Code Of Phone Number", example = "+66,+91")
	private String phoneCountryCode;

}
