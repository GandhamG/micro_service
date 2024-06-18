package com.oito.auth.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.CommunicationChannel;

public class UserUtils {
	private UserUtils() {
		throw new UnsupportedOperationException("Cannot initialize utils");
	}

	public static String randomPhone() {
		return StringUtils.substring(Long.toString(System.currentTimeMillis()), 0, 10);
	}

	public static boolean isValidEmail(final String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	public static String joinCommunicationChannel(final Set<CommunicationChannel> channelSet) {
		return channelSet.stream().map(String::valueOf).collect(Collectors.joining(Constants.SERPARATOR_COMMA));
	}
}
