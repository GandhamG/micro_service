package com.oito.auth.common.to;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.enumeration.UserCustomField;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMetadataUpdateRequest {

	private UserCustomField code;

	private String existingValue;

	private String newValue;

	private UserCustomField filterCode;

	private String filterValue;

	@JsonIgnore
	private Set<Long> userIds;
}
