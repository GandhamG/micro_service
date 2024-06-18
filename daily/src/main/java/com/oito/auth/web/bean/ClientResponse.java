package com.oito.auth.web.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ClientResponse {

	private Long userId;
	private String phoneNo;
	private String useremail;
	private String userName;
	private String clientType;
	private String clientId;
	private String secretKey;
	private String error;
	private String errorCode;
	private Map<String, String> clientMetadata;
}
