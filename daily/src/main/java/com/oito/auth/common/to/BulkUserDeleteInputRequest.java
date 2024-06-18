package com.oito.auth.common.to;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkUserDeleteInputRequest {

	private List<Long> userIds;
	private UserDeleteRequestStatus status;
	private String lang;
}
