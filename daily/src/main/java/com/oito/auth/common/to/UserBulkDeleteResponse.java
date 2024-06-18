package com.oito.auth.common.to;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBulkDeleteResponse {
	private List<UserDeleteResponse> responseList;
}
