package com.oito.auth.common.to;

import java.time.LocalDateTime;

import com.oito.auth.common.AuthProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginHistoryBean {

	private Long historyId;

	private Long userId;

	private String sessionId;

	private LocalDateTime loginTimestamp;

	private LocalDateTime logoutTimestamp;

	private AuthProvider authProvider;
}
