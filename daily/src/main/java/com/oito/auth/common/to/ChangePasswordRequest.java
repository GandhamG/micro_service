package com.oito.auth.common.to;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {

	private Long userId;
	private String useremail;
	private String userName;
	private String phoneNo;
	private String token;
	private String currentPassword;
	private String newPassword;
	private String lang;
}
