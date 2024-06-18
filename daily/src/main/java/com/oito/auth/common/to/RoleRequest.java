package com.oito.auth.common.to;

import java.util.List;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RoleRequest {

	private Long userId;

	private AuthUserType userType;

	private List<String> roleCodes;

	private List<Long> roleIds;

}
