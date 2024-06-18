package com.oito.auth.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.ObjectValidator;
import com.oito.auth.common.to.OTPLineVerificationRequest;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.OTPRequest;
import com.oito.auth.common.to.OTPTokenVerificationResponse;
import com.oito.auth.common.to.OTPVerificationRequest;
import com.oito.auth.common.to.OTPVerificationResponse;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.Status;
import com.oito.auth.exception.AuthException;
import com.oito.auth.service.SocialUserService;
import com.oito.auth.service.UserService;
import com.oito.common.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("user/OTP")
@Slf4j
public class OtpController {

	@Autowired
	private UserService userService;

	@Autowired
	private SocialUserService socialService;

	@Autowired
	private ObjectValidator validator;

	@PostMapping
	@ResponseBody
	public ResponseEntity<SimpleResponse> generateOTP(@RequestBody final OTPRequest request) {
		log.info("request login otp", request);
		try {
			return ResponseEntity.ok().body(userService.generateOTP(request));
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getMessage()));
		}
	}

	@PostMapping("resend")
	@ResponseBody
	public ResponseEntity<SimpleResponse> resendOTP(@RequestBody final OTPRequest request) {
		try {
			userService.resendOTP(request);
			return ResponseEntity.ok().body(SimpleResponse.success());
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getMessage()));
		}
	}

	@PostMapping("verify")
	@ResponseBody
	public ResponseEntity<SimpleResponse> verifyOTP(@RequestBody final OTPVerificationRequest request) {
		try {
			validator.validate(request);
			final var otpStatus = userService.verifyOTP(request.getOtpId().longValue(), request.getOtp());
			return ResponseEntity.ok().body(new OTPVerificationResponse(Status.SUCCESS, request.getOtpId(), otpStatus));
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final ApiException e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.failure(e.getErrorResponse().getMessage()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getMessage()));
		}
	}

	@PostMapping("verify/token")
	@ResponseBody
	public ResponseEntity<SimpleResponse> generateTokenAndVerifyOTP(
			@RequestBody final OTPPhoneVerificationRequest request) {
		try {
			return ResponseEntity.ok().body(
					new OTPTokenVerificationResponse(Status.SUCCESS, userService.generateTokenAndVerifyOTP(request)));
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final ApiException e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.failure(e.getErrorResponse().getMessage()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getMessage()));
		}
	}

	@PostMapping("verify/line")
	public ResponseEntity<SimpleResponse> verifyLineOtp(@RequestBody final OTPLineVerificationRequest request) {
		validator.validate(request);
		final var isOtpVerified = socialService.verifyLineOtp(request);
		return ResponseEntity.ok().body(new OTPVerificationResponse(Status.SUCCESS, request.getOtpId(), isOtpVerified));
	}

	private static void logAuthException(final AuthException e) {
		log.error("AuthException Occured", e);
	}

	private static void logException(final Exception e) {
		log.error("Exception Occured", e);
	}

	@PostMapping("validate-phone")
	public ResponseEntity<SimpleResponse> validatePhoneAndGenerateOTP(@RequestBody final OTPRequest request) {
		return ResponseEntity.ok().body(userService.validatePhoneAndGenerateOTP(request));
	}

	@GetMapping("verify-phone/{phoneNo}")
	public ResponseEntity<SimpleResponse> verifyPhoneAndGenerateOTP(@PathVariable final String phoneNo,
			@RequestParam(required = false) final String lang) {
		return ResponseEntity.ok().body(userService.verifyPhoneAndGenerateOTP(phoneNo,
				Objects.requireNonNullElse(lang, LocaleContextHolder.getLocale().getLanguage())));
	}

}
