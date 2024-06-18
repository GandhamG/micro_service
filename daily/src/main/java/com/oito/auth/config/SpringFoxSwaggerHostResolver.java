package com.oito.auth.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.OpenAPI;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Profile("!dev")
public class SpringFoxSwaggerHostResolver implements WebMvcOpenApiTransformationFilter {

	@Value("${domain.name}/${spring.application.name}")
	private String serviceHost;

	@Override
	public boolean supports(final DocumentationType delimiter) {
		return delimiter == DocumentationType.OAS_30;
	}

	@Override
	public OpenAPI transform(final OpenApiTransformationContext<HttpServletRequest> context) {
		final var swagger = context.getSpecification();
		swagger.getServers().get(0).setUrl(serviceHost);
		return swagger;
	}
}