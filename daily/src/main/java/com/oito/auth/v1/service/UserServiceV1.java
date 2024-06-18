/**
 *
 */
package com.oito.auth.v1.service;

import org.springframework.http.ResponseEntity;

import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.common.to.ChangePasswordRequestV1;
import com.oito.auth.v1.common.to.ProfileUpdateRequestVO;
import com.oito.auth.v1.common.to.ResetPasswordRequestV1;
import com.oito.auth.v1.common.to.ResetPasswordTokenRequestV1;
import com.oito.auth.v1.common.to.SignUpResponseV1;
import com.oito.auth.v1.common.to.UserLoggedInResponseV1;
import com.oito.auth.v1.common.to.UserSignUpRequestV1;

public interface UserServiceV1 {

	SignUpResponseV1 signup(UserSignUpRequestV1 request);

	ResponseEntity<AppUserTOV1> update(ProfileUpdateRequestVO vo);

	UserLoggedInResponseV1 getCurrentLoggedinUserData(final String accessToken);

	AppUserTOV1 createToken(ResetPasswordTokenRequestV1 createTokenRequest);

	SimpleResponse validateResetPasswordToken(String token);

	AppUserTOV1 resetPassword(ResetPasswordRequestV1 resetPasswordRequest);

	SimpleResponse changePassword(ChangePasswordRequestV1 changePasswordRequest);

}
