/**
 *
 */
package com.oito.auth.common;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.oito.common.exception.ApiException;
import com.oito.common.exception.response.ErrorResponse;

@Component
public class ObjectValidator {
	@SuppressWarnings("resource")
	public <T> void validate(final T validateObject) {
		final var factory = Validation.buildDefaultValidatorFactory();
		final var validator = factory.getValidator();
		final var errors = validator.validate(validateObject);
		if (CollectionUtils.isNotEmpty(errors)) {
			final var error = errors.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
			throw new ApiException(ErrorResponse.builder().message(error).status(HttpStatus.BAD_REQUEST).build());
		}
	}
}
