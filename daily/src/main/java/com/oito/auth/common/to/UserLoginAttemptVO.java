package com.oito.auth.common.to;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginAttemptVO {

	private String useremail;

	private String phoneNo;

	private Integer attempts = Integer.valueOf(1);

	private LocalDateTime lastModified;

	private String status;

}
