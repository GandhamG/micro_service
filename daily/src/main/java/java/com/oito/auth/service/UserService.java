/**
 *
 */
package com.oito.auth.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.oito.ResetPasswordApplication.BulkResetPasswordRequest;
import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangeEmailRequest;
import com.oito.auth.common.to.ChangePasswordRequest;
import com.oito.auth.common.to.ClientTO;
import com.oito.auth.common.to.ListResponse;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.OTPRequest;
import com.oito.auth.common.to.OTPResponse;
import com.oito.auth.common.to.SignUpResponse;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.UserIdentifierSearchRequest;
import com.oito.auth.common.to.UserListRequest;
import com.oito.auth.common.to.UserLoginAttemptVO;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.common.to.UserTypeRequest;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.data.User;
import com.oito.auth.exception.AuthException;

import lombok.NonNull;

/**
 * Basic User Services interface
 *
 * @author Dileep
 *
 */
public interface UserService {

	/**
	 * Signs up a new user to IDP and Persist the IDP user to application Database
	 *
	 * @param appUser
	 * @return
	 * @throws AuthException
	 */
	AppUserTO signUpUser(UserSignUpRequest request);

	SignUpResponse signup(UserSignUpRequest request);

	void validateDuplicateUser(final String userName, final String email, final String phone);

	AppUserTO saveClientUser(final ClientTO request);

	Optional<AppUserTO> getUserByEmail(String email);

	Optional<AppUserTO> getUserByUserName(String userName);

	Optional<AppUserTO> getUserByPhone(String phone);

	Optional<AppUserTO> getUserById(Long id);

	List<AppUserTO> getUsersByEmails(List<String> emailList);

	List<AppUserTO> getUsersByPhoneNos(List<String> phoneNos);

	Map<Long, AppUserTO> getUsersByUserIds(List<Long> userIds);

	AppUserTO resetPassword(ChangePasswordRequest changePasswordRequest);

	AppUserTO verify(String token);

	AppUserTO generateVerificationToken(String email);

	AppUserTO generateVerificationToken(ChangePasswordRequest changePasswordRequest);

	AppUserTO changePassword(ChangePasswordRequest appUserTO);

	AppUserTO update(AppUserTO appUser);

	AppUserTO generateResetPasswordToken(String userEmail, String phoneNo, String userName);

	boolean validateResetPasswordToken(String token);

	AppUserTO validateAndGenerateVerificationToken(ChangePasswordRequest changePasswordRequest, String token);

	void saveLoginHistory(final Long userId, final String sessionId, AuthProvider authProvider);

	SimpleResponse logOut(String sessionId, Long userId, String accessToken);

	List<String> getSupportedCountryCodes();

	OTPResponse generateOTP(OTPRequest otpRequest);

	boolean verifyOTP(long otpId, String otp);

	AppUserTO addUserTypeSecure(UserTypeRequest request);

	AppUserTO getUserForLogin(final UserLoginRequest request);

	AppUserTO verifyAuth(UserLoginRequest request);

	Optional<AppUserTO> getCurrentLoggedinUserData(final String accessToken);

	void populatePhoneNumber(UserLoginRequest request);

	String generateTokenAndVerifyOTP(OTPPhoneVerificationRequest request);

	AppUserTO changeEmail(ChangeEmailRequest changeEmailRequest);

	boolean matchPassword(final String password, final String encodedString);

	void addUserType(AppUserTO userTO, AuthUserType userType);

	UserTypeTO updateUserType(final UserTypeRequest userTypeRequest);

	ListResponse<AppUserTO> searchUser(UserListRequest userListRequest);

	void resendOTP(OTPRequest request);

	void changeEmailHistory(ChangeEmailRequest changeEmailRequest);

	void updateUserLoginDetails(AppUserTO userTO);

	AppUserTO updateUser(AppUserTO appUser);

	int updateLastAccessToken(Long userId, String currentToken, String newToken);

	void bulkUpdateForUserDelete(List<Long> userIds, UserStatus status);

	void bulkUpdateForUserUnDelete(List<Long> userIds, UserStatus status);

	Long searchUserCount(UserListRequest userListRequest);

	AppUserTO addUserType(UserTypeRequest userTypeRequest);

	Optional<AppUserTO> getImersonatedUserData();

	void resetPasswordAndNotify(String userIdentifier, final BulkResetPasswordRequest request);

	Optional<AppUserTO> getUserByEmailOrPhone(String value);

	Optional<UserTypeTO> filterUserTypeTO(final Set<UserTypeTO> userTypes, final AuthUserType userType);

	Optional<AppUserTO> getByIdAndType(Long userId, AuthUserType userType);

	List<AppUserTO> getByUserIdentifierList(UserIdentifierSearchRequest request);

	Optional<User> findByPhoneNoAndUserType(String phoneNo, AuthUserType userType);

	OTPResponse validatePhoneAndGenerateOTP(OTPRequest request);

	boolean isUserTypeExists(@NonNull AppUserTO user, AuthUserType type);

	UserTypeTO deleteSecondaryUser(UserTypeRequest userTypeRequest);

	User verifyOTPAndValidatePhone(UserLoginRequest request);

	OTPResponse verifyPhoneAndGenerateOTP(String phoneNo, String locale);

	Optional<Map<String, String>> getUserOtpByEmailOrPhone(final String value);

	List<AppUserTO> searchUserByPrivilege(final String privilegeCode, final String accessCode);

	UserLoginAttemptVO executeLoginAttempts(UserLoginAttemptVO loginAttemptVO);

}
