/**
 *
 */
package com.oito.auth.common;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Dileep
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

	public static final String TOKEN_EXPIRY_IN_MINUTES = "tokenExpiryInMinutes";
	public static final String RESET_PASSWORD_ACTION = "RESET_PASSWORD";
	public static final String CLIENT_REGISTRATION = "CLIENT_REGISTRATION";
	public static final String TOKEN_GENERATION = "TOKEN_GENERATION";

	public static final String GUEST_USER = "guestuser";

	public static final int PASSWORD_MIN_LENGTH = 8;
	public static final String REQUEST_FILE_PATH = "requestFilePath";

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String TOKEN_TYPE_KEY = "tokenType";
	public static final String USER_TYPE_KEY = "userType";
	public static final String USER_TYPE_FIELD = "userTypes";
	public static final String TYPE_FIELD = "type";
	public static final String PHONE_FIELD = "phoneNo";
	public static final String EMAIL_FIELD = "useremail";
	public static final String OTP_FIELD = "otpCode";
	protected static final String LAST_LOGIN_TIME_FIELD = "lastLoginTimestamp";
	protected static final String VERIFIED_FIELD = "verified";
	protected static final long ACTIVE_DAYS = 30;
	public static final String LIKE_FORMAT = "%%%s%%";
	public static final String STATUS_FIELD = "status";
	public static final String PRIVILEGE_ADMIN_CREATION = "USER_MANAGEMENT:FULL_ACCESS";
	public static final String EDIT_USER_FULL_ACCESS = "SECONDARY_USER_EDIT:FULL_ACCESS";

	public static final String SERPARATOR_COMMA = ",";
	public static final Pattern ASCII_REGEX_PATTERN = Pattern.compile("\\A\\p{ASCII}*\\z");
	public static final String SOCIAL_USER_TOKEN = "socialUserToken";
	public static final String REFRESH_TOKEN_KEY = "rId";
	public static final String DEFAULT_LANGUAGE = "th";

}
