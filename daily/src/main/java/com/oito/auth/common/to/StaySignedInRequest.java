package com.oito.auth.common.to;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class StaySignedInRequest {

	private Long userId;

	private AuthUserType userType;

	private String lastAccessToken;
}
