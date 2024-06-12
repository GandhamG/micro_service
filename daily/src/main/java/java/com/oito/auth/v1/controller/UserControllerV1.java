/**
 *
 */
package com.oito.auth.v1.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.ChangePasswordRequestV1;
import com.oito.auth.v1.common.to.ProfileUpdateRequestVO;
import com.oito.auth.v1.common.to.ResetPasswordRequestV1;
import com.oito.auth.v1.common.to.ResetPasswordTokenRequestV1;
import com.oito.auth.v1.common.to.SignUpResponseV1;
import com.oito.auth.v1.common.to.UserLoggedInResponseV1;
import com.oito.auth.v1.common.to.UserSignUpRequestV1;
import com.oito.auth.v1.service.UserServiceV1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("v1/user")
@Api(tags = "AuthenticationV1")
public class UserControllerV1 {

	@Autowired
	private UserServiceV1 userServiceV1;

	@PostMapping("signup")
	@ApiOperation(value = "Signup Users", nickname = "signup", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SignUpResponseV1 signup(@RequestBody final UserSignUpRequestV1 request) {
		return userServiceV1.signup(request);
	}

	@PutMapping
	@ApiOperation(value = "Update User", code = 200, httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AppUserTOV1> update(@Valid @RequestBody final ProfileUpdateRequestVO vo) {
		return userServiceV1.update(vo);
	}

	@GetMapping("userinfo")
	@ApiOperation(value = "Get Current Logged in User Data", code = 200, httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserLoggedInResponseV1 getCurrentLoggedinUserData(
			@RequestHeader(name = HttpHeaders.AUTHORIZATION) final String accessToken) {
		return userServiceV1.getCurrentLoggedinUserData(accessToken);
	}

	@PostMapping("reset-password/create-token")
	@ApiOperation(value = "Create Token", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTOV1 createToken(@RequestBody final ResetPasswordTokenRequestV1 createTokenRequest) {
		return userServiceV1.createToken(createTokenRequest);
	}

	@GetMapping("reset-password/token/validate/{token}")
	@ApiOperation(value = "Validate Reset Password Token", code = 200, httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse validateResetPasswordToken(@PathVariable final String token) {
		return userServiceV1.validateResetPasswordToken(token);
	}

	@PostMapping("reset-password")
	@ApiOperation(value = "Reset Password", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public AppUserTOV1 resetPassword(@RequestBody @Valid final ResetPasswordRequestV1 resetPasswordRequest) {
		return userServiceV1.resetPassword(resetPasswordRequest);
	}

	@PostMapping("change-password")
	@ApiOperation(value = "Change Password", code = 200, httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public SimpleResponse changePassword(@RequestBody @Valid final ChangePasswordRequestV1 changePasswordRequest) {
		return userServiceV1.changePassword(changePasswordRequest);
	}

}
