package com.oito.auth.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthUserType;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "oito.social.line")
@Getter
@Setter
public class LineConfigProperties {

	private Map<AuthUserType, String> clientId = Map.of();

}