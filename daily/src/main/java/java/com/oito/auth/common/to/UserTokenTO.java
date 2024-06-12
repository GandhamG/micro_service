package com.oito.auth.common.to;

import java.time.LocalDateTime;

import com.oito.auth.common.UserTokenType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTokenTO {

	private Long userTokenId;
	private Long userTypeId;
	private String token;
	private UserTokenType tokenType;
	private String macId;
	private LocalDateTime expiryTimeStamp;
	private AuditVO audit;

}
