package com.oito.auth.common.to;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.enumeration.CommunicationChannel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(exclude = "password")
public class UserSignUpRequest {

	private String orgIdfier;

	@Setter
	private String useremail;

	@Setter
	private String password;

	private Map<String, Object> source;

	@Setter
	private String userName;

	@Setter
	private String phoneNo;

	@Setter
	private String phoneCountryCode;

	@Setter
	private String fullName;

	@Setter
	private AuthUserType userType;

	private Map<String, String> customFields;

	private String impersonateUserEmail;

	private String preferredLanguage;

	@Setter
	private boolean socialFlag;

	@Setter
	private String socialId;

	@Setter
	private AuthProvider authProvider;

	@Setter
	private String facebookId;

	@Setter
	private String googleId;

	@Setter
	private String lineId;

	private boolean generatePassword;

	private List<PrivilegeRequest> privilegeList;

	private List<String> roleList;

	@JsonIgnore
	private AuditVO audit;

	@Setter
	private SignupOptions signupOptions;

	@Setter
	private Long otpId;

	@Setter
	private String otp;

	@Setter
	private Set<CommunicationChannel> communicationChannels = Set.of(CommunicationChannel.EMAIL,
			CommunicationChannel.SMS);

}
