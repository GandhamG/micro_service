package com.oito.auth.advice;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.common.exception.response.ErrorResponse;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class FeignExceptionHandler {

	@ExceptionHandler(FeignException.BadRequest.class)
	public Map<String, Object> handleFeignStatusException(final FeignException e, final HttpServletResponse response) {
		response.setStatus(e.status());
		return new JSONObject(e.contentUTF8()).toMap();
	}

	@ExceptionHandler(FeignException.Unauthorized.class)
	protected ResponseEntity<Object> handleFeignUnAuthorizedException(final FeignException ex) {
		log.error("FeignException-Unauthorized is caught in FeignExceptionHandler", ex);
		final var httpStatus = resolveHttpStatus(ex.status());
		final var errorResponse = ErrorResponse.builder().status(httpStatus)
				.errorCode(AuthErrorCode.UNAUTHORIZED_ACCESS.getCode()).message(ex.getLocalizedMessage())
				.detail(ex.getMessage()).exception(ex.getClass().getCanonicalName()).build();
		return new ResponseEntity<>(errorResponse, httpStatus);
	}

	private HttpStatus resolveHttpStatus(final int httpStatusCode) {
		final var httpStatus = HttpStatus.resolve(httpStatusCode);
		return httpStatus == null ? HttpStatus.OK : httpStatus;
	}
}