package com.oito.auth.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.passay.CharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.Rule;

import com.oito.auth.common.Constants;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;

public class PasswordHandler {

	private final org.passay.PasswordValidator validator;

	private final List<CharacterRule> characterRules;

	private final PasswordGenerator passwordGenerator;

	public PasswordHandler(final List<Rule> rules) {
		this.validator = new org.passay.PasswordValidator(rules);
		this.characterRules = rules.stream().filter(a -> a instanceof CharacterRule).map(p -> (CharacterRule) p)
				.collect(Collectors.toList());
		this.passwordGenerator = new PasswordGenerator();
	}

	public boolean validatePassword(final String password) {
		final var result = validator.validate(new PasswordData(password));
		if (result.isValid()) {
			return true;
		}
		throw new AuthException(AuthErrorCode.INVALID_PASSWORD, validator.getMessages(result));
	}

	public String generatePassword() {
		return passwordGenerator.generatePassword(Constants.PASSWORD_MIN_LENGTH, characterRules);
	}
}
