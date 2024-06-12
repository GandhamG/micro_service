/**
 *
 */
package com.oito.auth.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangeEmailRequest;
import com.oito.auth.common.to.ChangePasswordRequest;
import com.oito.auth.common.to.ListResponse;
import com.oito.auth.common.to.SignUpResponse;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.UserIdentifierSearchRequest;
import com.oito.auth.common.to.UserListRequest;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * User Service controller
 *
 * @author Dileep
 *
 */
@RestController
@RequestMapping("user")
@Api(tags = "User Management")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserControllerHelper userControllerHelper;

	/**
	 * Signs up a user
	 *
	 * @param userEntity {@link AppUserTO}
	 * @throws AuthException
	 * @throws AuthException {@link AuthException}
	 */
	@PostMapping("signup")
	@ResponseBody
	@ApiOperation(value = "Sign Up", nickname = "signup", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SignUpResponse signUp(@RequestBody final UserSignUpRequest request) {
		return userService.signup(request);
	}

	@GetMapping("{userId}")
	public AppUserTO getById(@PathVariable final Long userId) {
		return userService.getUserById(userId)
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
	}

	@GetMapping("{userId}/{userType}")
	public AppUserTO getByIdAndType(@PathVariable final Long userId, @PathVariable final AuthUserType userType) {
		return userService.getByIdAndType(userId, userType)
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
	}

	@PutMapping
	public ResponseEntity<AppUserTO> update(@RequestBody final AppUserTO appUserTO) {
		final AppUserTO response = null;
		try {
			log.info("Inside user update {}", appUserTO);
			return ResponseEntity.ok(userService.update(appUserTO));
		} catch (final AuthException exception) {
			log.error("Error while handling update request");
			logAuthException(exception);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(userControllerHelper.formErrorTO(exception));
		} catch (final Exception e) {
			log.error("Error while handling update request", e);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}
	}

	@GetMapping("userinfo")
	public AppUserTO getCurrentLoggedinUserData(@RequestHeader(HttpHeaders.AUTHORIZATION) final String accessToken) {
		return userService.getCurrentLoggedinUserData(accessToken).orElse(null);
	}

	@GetMapping("userinfo/impersonate")
	public AppUserTO getImersonatedUserData() {
		return userService.getImersonatedUserData().orElse(null);
	}

	@GetMapping("email")
	public AppUserTO getUserByEmail(@RequestParam final String email) {
		try {
			return userService.getUserByEmail(email).orElseGet(AppUserTO::new);
		} catch (final Exception exception) {
			log.error("Error while fetching user", exception);
			return new AppUserTO();
		}
	}

	@GetMapping("username")
	public AppUserTO getUserByName(@RequestParam final String userName) {
		return userService.getUserByUserName(userName)
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
	}

	@GetMapping("phone")
	public AppUserTO getUserByPhone(@RequestParam final String phone) {
		try {
			return userService.getUserByPhone(phone).orElseGet(AppUserTO::new);
		} catch (final Exception exception) {
			log.error("Error while fetching user", exception);
			return new AppUserTO();
		}
	}

	@GetMapping("email-or-phone")
	public AppUserTO getUserByEmailOrPhone(@RequestParam final String value) {
		return userService.getUserByEmailOrPhone(value).orElseGet(AppUserTO::new);
	}

	@PostMapping("list")
	public ListResponse<AppUserTO> searchUser(@RequestBody final UserListRequest userListRequest) {
		return userService.searchUser(userListRequest);
	}

	@PostMapping("list/id")
	public List<AppUserTO> getByUserIdentifierList(@RequestBody final UserIdentifierSearchRequest request) {
		return userService.getByUserIdentifierList(request);
	}

	@PostMapping("list/count")
	public Long searchUserCount(@RequestBody final UserListRequest userListRequest) {
		return userService.searchUserCount(userListRequest);
	}

	@PutMapping("email")
	public AppUserTO updateUserByEmail(@RequestBody final ChangeEmailRequest changeEmailRequest) {
		try {
			return userService.changeEmail(changeEmailRequest);
		} catch (final AuthException e) {
			logAuthException(e);
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			logException(e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@PostMapping("email/history")
	public SimpleResponse changeEmailHistory(@RequestBody final ChangeEmailRequest changeEmailRequest) {
		try {
			userService.changeEmailHistory(changeEmailRequest);
		} catch (final Exception e) {
			logException(e);
			return SimpleResponse.failure(AuthErrorCode.UNKNOWN_EXCEPTION);
		}
		return SimpleResponse.success();
	}

	@PostMapping("emails")
	public List<AppUserTO> getUserByEmails(@RequestBody final List<String> emailList) {
		try {
			return userService.getUsersByEmails(emailList);
		} catch (final AuthException e) {
			logAuthException(e);
			return List.of(userControllerHelper.formErrorTO(e));
		} catch (final Exception e) {
			logException(e);
			return List.of(userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION));
		}
	}

	@PostMapping("phones")
	public List<AppUserTO> getUserByPhoneNos(@RequestBody final List<String> phoneNos) {
		return userService.getUsersByPhoneNos(phoneNos);
	}

	@PostMapping("user-ids")
	public Map<Long, AppUserTO> getUserByUserIds(@RequestBody final List<Long> userIds) {
		try {
			return userService.getUsersByUserIds(userIds);
		} catch (final AuthException e) {
			logAuthException(e);
			return Map.of(NumberUtils.LONG_MINUS_ONE, userControllerHelper.formErrorTO(e));
		} catch (final Exception e) {
			logException(e);
			return Map.of(NumberUtils.LONG_MINUS_ONE,
					userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION));
		}
	}

	@GetMapping("verification-token/{email}")
	public SignUpResponse generateVerificationToken(@PathVariable final String email) {
		SignUpResponse response = null;
		try {
			final var userTO = userService.generateVerificationToken(email);
			response = SignUpResponse.success(userTO.getSignupToken());
		} catch (final AuthException exception) {
			response = SignUpResponse.failure(exception.getAuthErrorCode());
			log.error("AuthException while generating token", exception);
		} catch (final Exception e) {
			response = SignUpResponse.failure(e.getMessage());
			log.error("Exception while generating token", e);
		}
		return response;
	}

	@PostMapping("verification-token/create")
	public SignUpResponse generateVerificationToken(@RequestBody final ChangePasswordRequest changePasswordRequest) {
		SignUpResponse response = null;
		try {
			final var userTO = userService.generateVerificationToken(changePasswordRequest);
			response = SignUpResponse.success(userTO.getSignupToken());
		} catch (final AuthException exception) {
			response = SignUpResponse.failure(exception.getAuthErrorCode());
			log.error("Error while generating token", exception);
		} catch (final Exception e) {
			response = SignUpResponse.failure(e.getMessage());
			log.error("Error while generating token", e);
		}
		return response;
	}

	@PostMapping("verification-token")
	public AppUserTO verificationToken(@RequestBody final ChangePasswordRequest changePasswordRequest) {
		try {
			return userService.validateAndGenerateVerificationToken(changePasswordRequest,
					changePasswordRequest.getToken());
		} catch (final AuthException e) {
			logAuthException(e);
			return userControllerHelper.formErrorTO(e);

		} catch (final Exception e) {
			logException(e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	private static void logAuthException(final AuthException e) {
		log.error("AuthException Occured", e);
	}

	private static void logException(final Exception e) {
		log.error("Exception Occured", e);
	}

	@GetMapping("activate/{token}")
	public AppUserTO activate(@PathVariable final String token) {
		try {
			return userService.verify(token);
		} catch (final AuthException exception) {
			log.error("Error while verifying user", exception);
			return userControllerHelper.formErrorTO(exception);
		} catch (final Exception exception) {
			log.error("Error while verifying user", exception);
			return userControllerHelper.formErrorTO(exception, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@PostMapping("reset-password")
	public AppUserTO resetPassword(@RequestBody final ChangePasswordRequest changePasswordRequest) {
		AppUserTO userTO = null;
		try {
			userTO = userService.resetPassword(changePasswordRequest);
		} catch (final AuthException e) {
			logAuthException(e);
			userTO = userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			logException(e);
			userTO = userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
		return userTO;
	}

	@GetMapping("reset-password/token/{userEmail}")
	public AppUserTO createToken(@PathVariable final String userEmail) {
		try {
			return userService.generateResetPasswordToken(userEmail, null, null);
		} catch (final AuthException e) {
			log.error(e.getMessage());
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			logException(e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@PostMapping("reset-password/create-token")
	public AppUserTO createToken(@RequestBody final ChangePasswordRequest resetTokenRequest) {
		try {
			return userService.generateResetPasswordToken(resetTokenRequest.getUseremail(),
					resetTokenRequest.getPhoneNo(), resetTokenRequest.getUserName());
		} catch (final AuthException e) {
			log.error(e.getMessage());
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			logException(e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@GetMapping("reset-password/token/validate/{token}")
	public ResponseEntity<SimpleResponse> validateResetPasswordToken(@PathVariable final String token) {
		try {
			if (userService.validateResetPasswordToken(token)) {
				return ResponseEntity.ok().body(SimpleResponse.success());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.failure(AuthErrorCode.INVALID_TOKEN));
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.failure(AuthErrorCode.INVALID_TOKEN));
		}
	}

	@ApiOperation(value = "Change Password", nickname = "change-password", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PostMapping("change-password")
	public ResponseEntity<SimpleResponse> changePassword(
			@RequestBody final ChangePasswordRequest changePasswordRequest) {
		try {
			userService.changePassword(changePasswordRequest);
			return ResponseEntity.ok().body(SimpleResponse.success());
		} catch (final AuthException e) {
			logAuthException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getAuthErrorCode()));
		} catch (final Exception e) {
			logException(e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SimpleResponse.failure(e.getMessage()));
		}
	}

	@GetMapping("phone/countrycodes")
	public List<String> getSupportedCountryCodes() {
		try {
			return userService.getSupportedCountryCodes();
		} catch (final Exception exception) {
			log.error("Error reading supported country codes", exception);
			return Collections.emptyList();
		}
	}

	@PostMapping("phone/status")
	public boolean isPhoneVerified(@RequestBody final String phone) {
		try {
			return userService.getUserByPhone(phone).filter(user -> user.getPhoneVerified().booleanValue()).isPresent();
		} catch (final Exception exception) {
			log.error("Error fetching phone verification status", exception);
		}
		return false;
	}

	@PostMapping("verify")
	public AppUserTO verifyAuthentication(@RequestBody final UserLoginRequest request) {
		try {
			return userService.verifyAuth(request);
		} catch (final AuthException e) {
			logAuthException(e);
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			logException(e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@GetMapping("email-or-phone/otp")
	public ResponseEntity<Map<String, String>> getUserOtpByEmailOrPhone(@RequestParam final String value) {
		return ResponseEntity.ok(userService.getUserOtpByEmailOrPhone(value)
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB)));
	}

	@GetMapping("list-by-privilege/{privilegeCode}/{accessCode}")
	public List<AppUserTO> filterUserByMetadata(@PathVariable final String privilegeCode,
			@PathVariable final String accessCode) {
		return userService.searchUserByPrivilege(privilegeCode, accessCode);
	}
}
