package com.oito.auth.common.to;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMetadataRequest {
	private Long userId;

	private Map<String, String> customFields;
}
