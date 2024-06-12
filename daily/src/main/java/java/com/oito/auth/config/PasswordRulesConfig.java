package com.oito.auth.config;

import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.Rule;
import org.passay.WhitespaceRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.oito.auth.common.Constants;
import com.oito.auth.validator.PasswordHandler;

@Configuration
public class PasswordRulesConfig {

	@Bean
	PasswordHandler validator() {
		return new PasswordHandler(rules());
	}

	private List<Rule> rules() {
		return List.of(
				// length between 8 and maxvalue of Integer
				new LengthRule(Constants.PASSWORD_MIN_LENGTH, Integer.MAX_VALUE),
				// at least one Alphabetical character
				new CharacterRule(EnglishCharacterData.Alphabetical, 1),
				// at least one digit character
				new CharacterRule(EnglishCharacterData.Digit, 1),
				// no whitespace
				new WhitespaceRule());
	}
}
