package com.oito.auth.web.bean;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@ApiModel(value = "clientLoginResponse", description = "This class represents login response of the client")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@Getter
public class ClientLoginResponse {

	@ApiModelProperty(notes = "Access Token required to access the APIs", example = "zdsdfswwe123DFfewe", required = true)
	private String accessToken;

	@ApiModelProperty(notes = "Expiry Time stamp of accessToken", required = true)
	private LocalDateTime tokenExpiryTimestamp;

	@ApiModelProperty(notes = "Error Message if Login fails", example = "Invalid clientId/SecretKey", required = false)
	private String error;

	@ApiModelProperty(notes = "Error Code if Login fails", example = "INVALID_CLIENT_ID_OR_SECRET_KEY", required = false)
	private String errorCode;

	public ClientLoginResponse(final String accessToken, final LocalDateTime tokenExpiryTimestamp) {
		this.accessToken = accessToken;
		this.tokenExpiryTimestamp = tokenExpiryTimestamp;
	}

	public ClientLoginResponse(final String error, final String errorCode) {
		this.error = error;
		this.errorCode = errorCode;

	}

}