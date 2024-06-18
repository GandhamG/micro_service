package com.oito.auth.common.to;

import java.util.Set;

import com.oito.auth.common.enumeration.CommunicationChannel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeEmailRequest {
	private Long userId;

	private String useremail;

	private String newUseremail;

	private Set<CommunicationChannel> communicationChannels;
}
