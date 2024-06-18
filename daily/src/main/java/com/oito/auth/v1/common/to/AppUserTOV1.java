package com.oito.auth.v1.common.to;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.ErrorAction;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.json.UserTypeTOSerializer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(Include.NON_NULL)
public class AppUserTOV1 {

//Copied from APPUserTO
	private String orgIdfier;

	private Long userId;

	private String useremail;

	private UserStatus status;

	private String maskedUserEmail;

	private String password;

	@JsonIgnore
	private String userSecretHash;

	private String clientId;

	@JsonIgnore
	private String clientSecretHash;

	private String userName;

	private String phoneNo;

	private AuthUserType userType;

	private String fullName;

	private String accessToken;

	private String idToken;

	private String refreshToken;

	private Long expiresAt;

	private Boolean phoneVerified;

	private Boolean socialFlag;

	private String lineId;

	private String facebookId;

	private String googleId;

	private Map<String, String> customFields;

	private String impersonateUserEmail;

	private String preferredLanguage;

	private String lastAccessToken;

	private Boolean staySignedIn;

	private LocalDateTime tokenExpiryTimestamp;

	private Set<CommunicationChannel> communicationChannels;

	@JsonSerialize(using = UserTypeTOSerializer.class)
	private Set<UserTypeTO> userTypes;

	private String resetToken;

	private String signupToken;

	private List<String> privilegeList;

	private String sessionId;

	private String error;

	private String errorCode;

	private String warning;

	private String warningCode;

	private String phoneCountryCode;

	private Boolean verified;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
	private LocalDateTime lastLoginTimestamp;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
	private LocalDateTime createdTimestamp;

	private ErrorAction errorAction;

	private String socialErrorCode;

	public static AppUserTO empty() {
		final var user = new AppUserTO();
		user.setUserId(NumberUtils.LONG_MINUS_ONE);
		return user;
	}

	public boolean isEmpty() {
		return NumberUtils.LONG_MINUS_ONE.equals(this.getUserId());
	}

}
