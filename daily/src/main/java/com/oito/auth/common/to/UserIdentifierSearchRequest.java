package com.oito.auth.common.to;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIdentifierSearchRequest {

	private List<Long> userIdList;

	private List<String> userEmailList;

	private List<String> phoneNoList;

	private List<String> lineIdList;
}
