/**
 *
 */
package com.oito.auth.exception.errorcode;

import com.oito.auth.common.ErrorAction;
import com.oito.common.exception.errorcode.BaseCode;

import lombok.Getter;

/**
 * @author Dileep
 *
 */
@Getter
public enum AuthErrorCode implements BaseCode {

	LOGIN_RESET_PASSWORD_ENFORCE("LOGIN_RESET_PASSWORD_ENFORCE",
			"Our systems have been upgraded recently. We recommend you to reset your password to login to your account"),
	IDP_API_EXCEPTION("IDP_API_EXCEPTION", "IDP API Excepton"),
	USER_DELETE_REQUEST_OPEN("USER_DELETE_REQUEST_OPEN", "User delete request for this user is already open"),
	USER_DELETE_REQUEST_NO_OPEN("NO_OPEN_USER_DELETE_REQUEST",
			"There is no user delete request available for this user"),

	INVALID_APP_USER_DATA("INVALID_APP_USER_DATA", "Invalid user data"),
	APP_USER_CREDENTIALS_EMPTY("APP_USER_CREDENTIALS_EMPTY", "Login credentials are empty"),
	APP_USER_EMAIL_EMPTY("APP_USER_EMAIL", "Email is mandatory"),
	APP_USER_ID_EMPTY("APP_USER_ID_EMPTY", "User Id is mandatory"),
	APP_USER_PHONE_EMPTY("APP_USER_PHONE_EMPTY", "Phone number is mandatory"),
	APP_USER_TYPE_EMPTY("APP_USER_TYPE_EMPTY", "User Type is mandatory"),
	APP_USER_NAME_EMPTY("APP_USER_NAME_EMPTY", "User Name is mandatory"),
	APP_TOKEN_EMPTY("APP_TOKEN_EMPTY", "Token is mandatory"),
	UNABLE_TO_GET_USER_FROM_DB("UNABLE_TO_GET_USER_FROM_DB", "Unable to get user from database"),
	UNABLE_TO_GET_SESSION_ID_FROM_DB("UNABLE_TO_GET_SESSION_ID_FROM_DB",
			"Unable to get session id from User Login History Table"),
	USER_DISABLED("USER_DISABLED", "User is disabled, Please Contact Administrator"),
	APP_USER_PASSWORD_EMPTY("APP_USER_PASSWORD", "Password is mandatory"),
	USER_NOT_FOUND("USER_NOT_FOUND", "Unable to locate User"),
	UNABLE_TO_SAVE_LOGIN_HISTORY("UNABLE_TO_SAVE_LOGIN_HISTORY", "Uknown error occured while saving user history"),
	INVALID_USER_EMAIL("INVALID_USER_EMAIL", "Email provided not matching any entry"),
	INVALID_CHAR_IN_EMAIL("INVALID_CHAR_IN_EMAIL", "Email contains invalid characters"),
	UNKNOWN_EXCEPTION("UNKNOWN_EXCEPTION", "Unknown Error Occurred"),
	INVALID_PASSWORD("INVALID_PASSWORD", "The password used for authentication is invalid"),
	INVALID_PHONE("INVALID_PHONE", "The phone number given is invalid"),
	INVALID_TOKEN("INVALID_TOKEN", "Token is invalid"),
	INVALID_UPDATE_KEY("INVALID_UPDATE_KEY", "Ivalid Key for update. Either User Email or User ID is mandatory"),
	OTP_RESEND_LIMIT_EXCEEDED("OTP_RESEND_LIMIT_EXCEEDED", "OTP Resend limit exceeded"),
	CLIENT_LOGIN_FAILURE("CLIENT_LOGIN_FAILURE", "Unable to login using client credentials"),
	STRING_MASKING_FAILURE("STRING_MASKING_FAILURE", "End index cannot be greater than start index"),

	TOO_MANY_USERS_FOUND("TOO_MANY_USERS_FOUND", "Multiple accounts found, cannot proceed"),

	ADD_USER_TYPE_FAILURE("ADD_USER_TYPE_FAILURE", "Failed to add new user type"),

	USER_EXISTS_WITH_PHONE_EMAIL("USER_EXISTS_WITH_PHONE_EMAIL",
			"You already have an account with this email & phone number. Please login to your account"),
	USER_EXISTS_WITH_PHONE("USER_EXISTS_WITH_PHONE",
			"You already have an account with this phone number. Please login to your account or sign up using a different number"),
	USER_EXISTS_WITH_EMAIL("USER_EXISTS_WITH_EMAIL",
			"You already have an account with this email. Please login to your account or sign up using a different email"),
	USER_EXISTS_WITH_USERNAME("USER_EXISTS_WITH_USERNAME",
			"You already have an account with this User Name. Please login to your account or sign up using a different email"),
	OTHER_PORTAL_USER_EXISTS_WITH_PHONE_EMAIL("OTHER_PORTAL_USER_EXISTS_WITH_PHONE_EMAIL",
			"You already have a shopping/professional/seller account with this email & phone number. Please type your account password to complete setup & login",
			ErrorAction.GET_PASSWORD),
	OTHER_PORTAL_USER_EXISTS_WITH_EMAIL("OTHER_PORTAL_USER_EXISTS_WITH_EMAIL",
			"You already have a shopping/professional/seller account with this email. Please type your account password to complete setup & login",
			ErrorAction.GET_PASSWORD),
	OTHER_PORTAL_USER_EXISTS_WITH_PHONE("OTHER_PORTAL_USER_EXISTS_WITH_PHONE",
			"You already have a shopping/professional/seller account"
					+ " with this phone number. Please type your complete email & password to complete setup & login",
			ErrorAction.GET_EMAIL_PASSWORD),
	OTHER_PORTAL_USER_EXISTS_WITH_USERNAME("OTHER_PORTAL_USER_EXISTS_WITH_USERNAME",
			"You already have a shopping/professional/seller account with this User Name. Please type your account password to complete setup & login",
			ErrorAction.GET_PASSWORD),
	SCATTERED_USER_INFO("SCATTERED_USER_INFO",
			"You already have an account with this email & phone number. Please login to your account"),
	INVALID_ROLE("INVALID_ROLE", "Invalid role"),
	INVALID_PRIVILEGE_REQUEST("INVALID_PRIVILEGE_REQUEST",
			"Privilege for given resource code and access code doesn't exist"),
	USER_TYPE_NOT_FOUND("USER_TYPE_NOT_FOUND", "Requested user type not found"),
	OTP_NOT_FOUND_OR_EXPIRED("OTP_NOT_FOUND_OR_EXPIRED", "Requested OTP record not found or expired"),
	INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN", "Invalid Access Token"),
	WARNING_INVALID_PHONE("WARNING_INVALID_PHONE",
			"We observe that we do not have your correct phone number in our system"),
	WARNING_INVALID_CHARS_EMAIL("WARNING_INVALID_CHARS_EMAIL", "We observed invalid characters in your email"),

	UN_VERIFIED_USER("UN_VERIFIED_USER", "User is not verified"),
	DELETED_USER("DELETED_USER", "User is already deleted"),
	EMPTY_PASSWORD("EMPTY_PASSWORD",
			"You might be logged in using your Social account and not allowed to change your password"),
	MISSING_PARAMS("MISSING_PARAMS", "Parameters are missing"),
	UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "Unauthorized access"),
	DUPLICATE_KEY("DUPLICATE_KEY", "Duplicate Data Found while Saving"),
	LINE_ID_NOT_FOUND("LINE_ID_NOT_FOUND", "We are unable to locate the line id from database"),
	INVALID_SEARCH_REQUEST("INVALID_SEARCH_REQUEST", "Invalid search request"),
	INVALID_PHONE_LENGTH("INVALID_PHONE_LENGTH", "Invalid Phone Length"),
	PHONE_MULTIPLE_ACCOUNTS("PHONE_MULTIPLE_ACCOUNTS",
			"We observed that phone number is associated with mulitple accounts in our system"),
	USER_TYPE_ID_INVALID("USER_TYPE_ID_INVALID", "User Type id passed doesnt exists in database"),
	REFRESH_TOKEN_NOT_FOUND("REFRESH_TOKEN_NOT_FOUND", "Refresh Token not found in database"),
	MULTIPLE_REFRESH_TOKENS_FOUND("MULTIPLE_REFRESH_TOKENS_FOUND", "Multiple refersh token found for same user"),
	REFRESH_TOKEN_ALREADY_EXISTS_FOR_MAC_AND_TOKEN_TYPE("REFRESH_TOKEN_ALREADY_EXISTS_FOR_MAC_AND_TOKEN_TYPE",
			"Cannot create token as same token already exists for same macid and tokentype"),
	OTP_NOT_FOUND("OTP_NOT_FOUND", "OTP not found for phone or email"),
	PARTNER_DETAILS_MISSING("PARTNER_DETAILS_MISSING", "Client-Id/Secret-Key missing"),
	PARTNER_NOT_FOUND("PARTNER_NOT_FOUND", "Partner details not found in database");

	private String errorCode;
	private String errorMessage;
	private ErrorAction errorAction;

	private String code;
	private int httpStatusCode;
	private String message;
	private String messageKey;

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	AuthErrorCode(final String errorCode, final String errorMessage, final ErrorAction errorAction) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.errorAction = errorAction;
		this.code = errorCode;
		this.message = errorMessage;
		this.httpStatusCode = 200;
	}

	AuthErrorCode(final String errorCode, final String errorMessage) {
		this(errorCode, errorMessage, ErrorAction.SHOW_ERROR);
	}

}