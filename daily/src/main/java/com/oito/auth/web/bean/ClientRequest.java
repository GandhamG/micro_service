package com.oito.auth.web.bean;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.enumeration.CommunicationChannel;

import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class ClientRequest {

	private String useremail;
	private String userName;
	private String phoneNo;
	private Integer tokenExpiryInMinutes;
	private AuthUserType clientType;
	private Map<String, String> clientMetadata;
	private Set<CommunicationChannel> communicationChannels = Set.of(CommunicationChannel.EMAIL,
			CommunicationChannel.SMS);
}
