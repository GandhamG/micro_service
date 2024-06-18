package com.oito.auth.util;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;

public class MaskUtils {

	private MaskUtils() {
		throw new AssertionError();
	}

	public static String maskEmailAddress(final String strEmail, final char maskChar) {

		if (StringUtils.isBlank(strEmail)) {
			return StringUtils.EMPTY;
		}

		final var parts = strEmail.split("@");

		// mask first part
		var strId = "";
		if (parts[0].length() < 4) {
			strId = maskString(parts[0], 0, parts[0].length(), maskChar);
		} else {
			strId = maskString(parts[0], 1, parts[0].length() - 1, maskChar);
		}
		// now append the domain part to the masked id part
		return strId + "@" + parts[1];
	}

	public static String maskPhone(final String phone) {
		final var start = phone.startsWith("+") ? 5 : 2;
		final var end = phone.length() - 2;
		return maskString(phone, start, end, '*');
	}

	public static String maskString(final String strText, int start, int end, final char maskChar) {

		if (Strings.isNullOrEmpty(strText)) {
			return "";
		}

		if (start < 0) {
			start = 0;
		}

		if (end > strText.length()) {
			end = strText.length();
		}

		if (start > end) {
			throw new AuthException(AuthErrorCode.STRING_MASKING_FAILURE);
		}

		final var maskLength = end - start;

		if (maskLength == 0) {
			return strText;
		}

		final var sbMaskString = new StringBuilder(maskLength);

		for (var i = 0; i < maskLength; i++) {
			sbMaskString.append(maskChar);
		}

		return strText.substring(0, start) + sbMaskString.toString() + strText.substring(start + maskLength);
	}
}
