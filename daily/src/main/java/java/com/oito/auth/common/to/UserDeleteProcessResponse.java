package com.oito.auth.common.to;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteProcessResponse {

	private String status;
	private String fullName;
	private String userEmail;
	private String errorCode;
	private String errorMessage;

}
