/**
 *
 */
package com.oito.auth.v1.common.to;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.AuditVO;
import com.oito.auth.common.to.PrivilegeRequest;
import com.oito.auth.common.to.SignupOptions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(exclude = "password")
@ApiModel(value = "UserSignUpRequestV1", description = "This represents the user signup request version1")
public class UserSignUpRequestV1 {

	@Setter
	@ApiModelProperty(notes = "User Email", example = "useremail@domain.com")
	private String useremail;

	@Setter
	@ApiModelProperty(notes = "User Password, required if not OTP based signup", example = "password", required = true)
	private String password;

	@Setter
	@ApiModelProperty(notes = "User Phone Number", example = "+61123456789", required = true)
	private String phoneNo;

	@Setter
	@ApiModelProperty(notes = "Country Code Of Phone Number", example = "+61,+91", required = true)
	private String phoneCountryCode;

	@Setter
	@ApiModelProperty(notes = "User Full Name", example = "Sam Curran")
	private String fullName;

	@Setter
	@ApiModelProperty(notes = "User Type", example = "BUYER", required = true, allowableValues = "BUYER,SELLER,PROFESSIONAL")
	private AuthUserType userType;

	@ApiModelProperty(notes = "Custome Fields", example = "{\"field1\":\"value1\"}")
	private Map<String, String> customFields;

	@ApiModelProperty(notes = "User Preferred Language", example = "th", allowableValues = "en,th")
	private String preferredLanguage;

	@ApiModelProperty(notes = "Privilege List", example = "[{\"resourceCode\":\"RC001\", \"accessCode\":\"AC001\" }]")
	private List<PrivilegeRequest> privilegeList;

	@ApiModelProperty(notes = "Role List", example = "[\"Admin\",\"Manager\"]")
	private List<String> roleList;

	@Setter
	@ApiModelProperty(notes = "Otp Id, required if OTP based signup", example = "3112", required = true)
	private Long otpId;

	@Setter
	@ApiModelProperty(notes = "Otp Code, required if OTP based signup", example = "203685", required = true)
	private String otp;

	@JsonIgnore
	private AuditVO audit;

	@Setter
	@JsonIgnore
	private SignupOptions signupOptions;

}
