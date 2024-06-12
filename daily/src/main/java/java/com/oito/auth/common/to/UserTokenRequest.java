package com.oito.auth.common.to;

import com.oito.auth.common.UserTokenType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserTokenRequest {
	private Long userTypeId;
	private UserTokenType tokenType;
	private String macId;
	private String token;
}
