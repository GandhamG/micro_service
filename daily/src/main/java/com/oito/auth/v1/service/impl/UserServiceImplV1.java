/**
 *
 */
package com.oito.auth.v1.service.impl;

import static com.oito.common.util.Booleans.getIfTrueOrElse;
import static com.oito.common.util.Nulls.ifNonNull;
import static com.oito.common.util.Nulls.orElseThrow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.SignupOptions;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserService;
import com.oito.auth.service.impl.UserContextService;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.ChangePasswordRequestV1;
import com.oito.auth.v1.common.to.ProfileUpdateRequestVO;
import com.oito.auth.v1.common.to.ResetPasswordRequestV1;
import com.oito.auth.v1.common.to.ResetPasswordTokenRequestV1;
import com.oito.auth.v1.common.to.SignUpResponseV1;
import com.oito.auth.v1.common.to.UserLoggedInResponseV1;
import com.oito.auth.v1.common.to.UserSignUpRequestV1;
import com.oito.auth.v1.mapper.AppUserTOV1Mapper;
import com.oito.auth.v1.mapper.ChangePasswordRequestMapper;
import com.oito.auth.v1.mapper.ProfileUpdateMapper;
import com.oito.auth.v1.mapper.SignUpResponseMapperV1;
import com.oito.auth.v1.mapper.UserProfileResponseMapper;
import com.oito.auth.v1.mapper.UserSignUpRequestMapperV1;
import com.oito.auth.v1.service.UserServiceV1;
import com.oito.common.exception.ApiException;
import com.oito.common.exception.errorcode.ErrorCode;
import com.oito.common.exception.response.ErrorResponse;
import com.oito.common.usercontext.UserContextStore;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImplV1 implements UserServiceV1 {

	@Autowired
	private UserSignUpRequestMapperV1 userSignUpRequestMapperV1;

	@Autowired
	private SignUpResponseMapperV1 signUpResponseMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private UserContextStore userContextStore;

	@Autowired
	private ProfileUpdateMapper updateMapper;

	@Autowired
	private AppUserTOV1Mapper toV1Mapper;

	@Autowired
	private UserProfileResponseMapper responseMapper;

	@Value("${oito.signup.optional-fields.email.user-types:}")
	private List<AuthUserType> optionalEmailUserTypes;

	@Autowired
	private ChangePasswordRequestMapper changePasswordRequestMapper;

	@Autowired
	private UserContextService userContextService;

	@Override
	@Transactional
	public SignUpResponseV1 signup(final UserSignUpRequestV1 mobileRequest) {
		mobileRequest.setSignupOptions(new SignupOptions(optionalEmailUserTypes));
		final var response = userService.signup(userSignUpRequestMapperV1.toEntity(mobileRequest));
		log.debug("Signup is successful for the requeset {}", mobileRequest);
		return signUpResponseMapper.toEntity(response);
	}

	@Override
	public ResponseEntity<AppUserTOV1> update(final ProfileUpdateRequestVO vo) {
		vo.setUserId(userContextStore.getUserContext().getUserId());
		ifNonNull(vo.getPhoneNo(), () -> vo.setPhoneCountryCode(vo.getPhoneNo().substring(0, 3)));
		try {
			final var appUserTO = userService.update(updateMapper.toEntity(vo));
			orElseThrow(appUserTO.getErrorCode(), () -> new ApiException(ErrorResponse.builder()
					.errorCode(appUserTO.getErrorCode()).message(appUserTO.getError()).status(HttpStatus.OK).build()));
			return ResponseEntity.ok(toV1Mapper.toEntity(appUserTO));
		} catch (final ApiException ae) {
			throw new ApiException(ErrorResponse.builder().errorCode(ae.getErrorCodeString()).status(HttpStatus.OK)
					.message(ae.getMessage()).build());
		}
	}

	@Override
	public UserLoggedInResponseV1 getCurrentLoggedinUserData(final String accessToken) {
		return responseMapper.toVO(userService.getCurrentLoggedinUserData(accessToken)
				.orElseThrow(() -> new ApiException(ErrorCode.INVALID_TOKEN)));
	}

	@Override
	public AppUserTOV1 createToken(final ResetPasswordTokenRequestV1 request) {
		return toV1Mapper.toEntity(userService.generateResetPasswordToken(request.getUseremail(), request.getPhoneNo(),
				request.getUserName()));
	}

	@Override
	public SimpleResponse validateResetPasswordToken(final String token) {
		return getIfTrueOrElse(userService.validateResetPasswordToken(token), SimpleResponse::success,
				() -> SimpleResponse.failure(AuthErrorCode.INVALID_TOKEN));
	}

	@Override
	public AppUserTOV1 resetPassword(final ResetPasswordRequestV1 resetPasswordRequest) {
		return toV1Mapper
				.toEntity(userService.resetPassword(changePasswordRequestMapper.toRequest(resetPasswordRequest)));
	}

	@Override
	public SimpleResponse changePassword(final ChangePasswordRequestV1 request) {
		final var changePasswordRequest = changePasswordRequestMapper.toRequest(request);
		changePasswordRequest.setUserId(userContextService.getUserId());
		userService.changePassword(changePasswordRequest);
		return SimpleResponse.success();
	}

}
