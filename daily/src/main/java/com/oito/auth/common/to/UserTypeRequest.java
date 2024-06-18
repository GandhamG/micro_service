package com.oito.auth.common.to;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
public class UserTypeRequest {

	private AuthUserType userType;

	private String useremail;

	private String phoneNo;

	private String password;

	private boolean enabled;

	private List<String> roleList;

	private List<PrivilegeRequest> privilegeList;

	private Map<String, String> customFields;

	private Long otpId;

	private String otp;

	@JsonIgnore
	private AuditVO audit;

}
