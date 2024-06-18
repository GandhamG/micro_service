package com.oito.auth.web.bean;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "clientLoginRequest", description = "This class represents login response of the client")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class ClientLoginRequest {

	@ApiModelProperty(notes = "Client Id of the Client", example = "zdsdfswwe123DFfewe", required = true)
	@NotNull
	private String clientId;

	@ApiModelProperty(notes = "Client Secret of the Client", example = "Aqsdfshj&66634dfgdgfd000wewrwe", required = true)
	@NotNull
	private String secretKey;
}