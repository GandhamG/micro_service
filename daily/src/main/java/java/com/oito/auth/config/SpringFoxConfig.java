package com.oito.auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {

	private static final String JWT = "JWT";

	@Bean
	public Docket swaggerSpringMvcPlugin() {
		return new Docket(DocumentationType.OAS_30).apiInfo(apiInfo()).securitySchemes(securitySchemes())
				.securityContexts(securityContexts()).select()
				.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).build();
	}

	private List<SecurityContext> securityContexts() {
		return List.of(SecurityContext.builder()
				.securityReferences(
						List.of(SecurityReference.builder().scopes(new AuthorizationScope[0]).reference(JWT).build()))
				.operationSelector(operationContext -> operationContext.requestMappingPattern().matches("/.*"))
				.build());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Authentication API Documentation",
				"This api helps you to  access different authentication end points", "v1", "", new Contact("", "", ""),
				"License of API", "API license URL", List.of());
	}

	private List<SecurityScheme> securitySchemes() {
		return List.of(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name(JWT).build());
	}

}
