package com.oito.auth.common.to;

import static com.oito.auth.common.AuthUserType.BUYER;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.UserActivityStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UserListRequest {
	private int itemsPerPage;
	private int page;
	private String searchField;
	private String searchText;
	private UserActivityStatus userStatus;
	private String orderBy = Constants.EMAIL_FIELD;
	private AuthUserType searchUserType = BUYER;
}
