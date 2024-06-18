package com.oito.auth.common.to;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.enumeration.CommunicationChannel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = { "secretKey", "password" })
public class ClientTO {

	private Long userId;

	private AuthUserType userType;

	private String useremail;

	private String userName;

	private String password;

	private String phoneNo;

	private AuthUserType clientType;

	private String clientId;

	private String secretKey;

	private String accessToken;

	private Map<String, String> clientMetadata;

	private Set<CommunicationChannel> communicationChannels;

	private Integer tokenExpiryInMinutes;

	@JsonIgnore
	private AuditVO audit;

}
