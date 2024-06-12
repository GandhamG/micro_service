/**
 *
 */
package com.oito.auth.validator;

import static java.util.Objects.isNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.CharMatcher;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.UserCustomField;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangePasswordRequest;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.impl.UserContextService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dileep
 *
 */
@Component
@Slf4j
public class UserValidator {

	@Value("#{'${oito.email.disallowed-chars}'.toCharArray()}")
	private char[] emailDisallowedChars;

	@Value("${oito.phone.max-digit}")
	private int phoneMaxDigit;

	@Value("${oito.phone.mandatory:#{true}}")
	private boolean isPhoneMandatory;

	@Value("${oito.phone.min-digit}")
	private int phoneMinDigit;

	@Autowired
	private PasswordHandler passwordValidator;

	@Autowired
	private UserContextService userContextService;

	@Value("${oito.phone.country-codes}")
	private List<String> supportedCountryCodes;

	private static PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

	public void validateLoginRequest(final UserLoginRequest request) {
		validateMandatoryFields(false, request.getPassword(), request.getUserType());
		if (StringUtils.isAllBlank(request.getUseremail(), request.getPhoneNo(), request.getUserName())) {
			throw new AuthException(AuthErrorCode.APP_USER_CREDENTIALS_EMPTY);
		}
	}

	public void validateSignUpRequest(final UserSignUpRequest request) {
		validateSignupMandatoryFields(request);
		validateSignupEmail(request);

		if (StringUtils.isBlank(request.getPhoneNo()) && isPhoneMandatory) {
			throw new AuthException(AuthErrorCode.APP_USER_PHONE_EMPTY);
		}
		if (StringUtils.isNotBlank(request.getPassword())) {
			passwordValidator.validatePassword(request.getPassword());
		}
		validatePhone(request.getPhoneNo(), request.getPhoneCountryCode());
	}

	private void validateSignupMandatoryFields(final UserSignUpRequest request) {
		validateUserType(request.getUserType());
		final var otpId = request.getOtpId();
		if (isNull(otpId) || isNull(request.getOtp())) {
			validatePassword(request.isSocialFlag(), request.getPassword());
		}
	}

	private void validateUserType(final AuthUserType userType) {
		if (isNull(userType)) {
			throw new AuthException(AuthErrorCode.APP_USER_TYPE_EMPTY);
		}
	}

	private void validatePassword(final boolean socialFlag, final String password) {
		if (!socialFlag && StringUtils.isBlank(password)) {
			throw new AuthException(AuthErrorCode.APP_USER_PASSWORD_EMPTY);
		}
	}

	public String replaceFirstNonASCIICharacter(final String text) {
		if (StringUtils.isBlank(text)) {
			return text;
		}
		final var textFirstChar = text.substring(0, 1);
		if (!Constants.ASCII_REGEX_PATTERN.matcher(textFirstChar).matches()) {
			final var textRemaining = text.substring(1);
			return CharMatcher.ascii().retainFrom(textFirstChar) + textRemaining;
		}
		return text;
	}

	public void validateEditAdminPrivilege() {
		userContextService.verifyAdminAccess();
		userContextService.verifyPrivilege(Constants.PRIVILEGE_ADMIN_CREATION);
	}

	public void validateAdminUpate(final Map<String, String> customFields) {
		if (MapUtils.emptyIfNull(customFields).containsKey(UserCustomField.IS_ADMIN.getName())) {
			validateEditAdminPrivilege();
		}
	}

	private void validateSignupEmail(final UserSignUpRequest request) {
		final var options = request.getSignupOptions();
		if (isNull(options) || !options.getOptionalEmailUserTypes().contains(request.getUserType())) {
			validateEmail(request.getUseremail());
		}
		validateEmailDisallowedCharacters(request.getUseremail());
	}

	private void validateEmailDisallowedCharacters(final String email) {
		if (StringUtils.containsAny(email, emailDisallowedChars)) {
			throw new AuthException(AuthErrorCode.INVALID_CHAR_IN_EMAIL,
					Arrays.toString(emailDisallowedChars) + " are not allowed in User Email");
		}
	}

	public void validateEmail(final String email) {
		if (StringUtils.isBlank(email)) {
			throw new AuthException(AuthErrorCode.APP_USER_EMAIL_EMPTY);
		}
		validateEmailDisallowedCharacters(email);
	}

	/*
	 *
	 */
	public void validateChangePassword(final ChangePasswordRequest appUserTO) {
		if (null == appUserTO) {
			throw new AuthException(AuthErrorCode.INVALID_APP_USER_DATA);
		}
		passwordValidator.validatePassword(appUserTO.getNewPassword());
	}

	/*
	 *
	 */
	public void validateResetPassword(final ChangePasswordRequest appUserTO) {
		if (null == appUserTO) {
			throw new AuthException(AuthErrorCode.INVALID_APP_USER_DATA);
		}
		if (StringUtils.isBlank(appUserTO.getToken())) {
			throw new AuthException(AuthErrorCode.APP_TOKEN_EMPTY);
		}
		passwordValidator.validatePassword(appUserTO.getNewPassword());
	}

	public void validateUpdate(final AppUserTO appUserTO) {
		if (null == appUserTO) {
			throw new AuthException(AuthErrorCode.INVALID_APP_USER_DATA);
		}
		if (StringUtils.isAllBlank(appUserTO.getUseremail(), appUserTO.getUserName())
				&& (appUserTO.getUserId() == null || Long.valueOf(0).equals(appUserTO.getUserId()))) {
			throw new AuthException(AuthErrorCode.INVALID_UPDATE_KEY);
		}
	}

	private void validateMandatoryFields(final boolean socialFlag, final String password, final AuthUserType userType) {
		validatePassword(socialFlag, password);
		validateUserType(userType);
	}

	public void validatePhone(final String phoneNo, final String phoneCountryCode) {
		if (isInvalidPhoneNo(phoneNo, phoneCountryCode)) {
			log.error("Invalid Phone number {} phoneCountryCode {} maxLength {} minLength {}", phoneNo,
					phoneCountryCode, phoneMaxDigit, phoneMinDigit);
			throw new AuthException(AuthErrorCode.INVALID_PHONE);
		}
	}

	public void validateCustomSignUpRequest(final UserSignUpRequest request) {
		if (StringUtils.isBlank(request.getUserName())) {
			throw new AuthException(AuthErrorCode.APP_USER_NAME_EMPTY);
		}
	}

	public void populateUserWarnings(final AppUserTO userTO, final boolean isEmailChanged) {
		populatePhoneNoWarnings(userTO);
		populateEmailWarnings(userTO, isEmailChanged);
	}

	public void populateEmailWarnings(final AppUserTO responseVO, final boolean isEmailChanged) {
		if (isEmailChanged) {
			responseVO.setWarning(AuthErrorCode.WARNING_INVALID_CHARS_EMAIL.getMessage());
			responseVO.setWarningCode(AuthErrorCode.WARNING_INVALID_CHARS_EMAIL.getCode());
		}
	}

	private void populatePhoneNoWarnings(final AppUserTO userTO) {
		if (isInvalidPhoneNo(userTO.getPhoneNo(), userTO.getPhoneCountryCode())) {
			userTO.setWarning(AuthErrorCode.WARNING_INVALID_PHONE.getErrorMessage());
			userTO.setWarningCode(AuthErrorCode.WARNING_INVALID_PHONE.getCode());
		}
	}

	private boolean isInvalidPhoneNo(final String phoneNo, final String phoneCountryCode) {
		if (StringUtils.isEmpty(phoneNo)) {
			return false;
		}
		return (StringUtils.isEmpty(phoneCountryCode) || !phoneNo.startsWith(phoneCountryCode)
				|| !supportedCountryCodes.contains(phoneCountryCode)
				|| validatePhoneNoLength(phoneNo, phoneCountryCode));
	}

	private boolean validatePhoneNoLength(final String phoneNo, final String phoneCountryCode) {
		final var phoneLength = phoneNo.replaceFirst(Pattern.quote(phoneCountryCode), "").length();
		if (phoneMaxDigit == phoneMinDigit) {
			return phoneLength != phoneMaxDigit;
		}
		if (phoneLength < phoneMinDigit || phoneLength > phoneMaxDigit) {
			log.error("Invalid Phone number {} phoneCountryCode {} maxLength {} minLength {}", phoneNo,
					phoneCountryCode, phoneMaxDigit, phoneMinDigit);
			throw new AuthException(AuthErrorCode.INVALID_PHONE_LENGTH);
		}
		return false;
	}

	@SneakyThrows
	public void validatePhone(final String phoneNo) {
		var numberProto = phoneUtil.parse(phoneNo, "");
		validatePhone(phoneNo, "+" + numberProto.getCountryCode());
	}

}
