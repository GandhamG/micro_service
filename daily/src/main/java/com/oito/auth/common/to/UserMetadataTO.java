package com.oito.auth.common.to;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMetadataTO {

	private Long metadataId;

	private Long userId;

	private String code;

	private String value;

	private AuditVO audit;

}
