package com.oito.auth.common.to;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteInputRequest {

	private Long userId;
	private Long userDeleteRequestId;
	private UserDeleteRequestStatus status = UserDeleteRequestStatus.OPEN;
	private String lang;
	private String emailID;
	private String reason;
	private String accessToken;
	private String fullName;
	private Boolean ignoreLoggedIn;
	private String useremail;
	private String phoneNumber;
	private LocalDateTime processedTimestamp;
}
