package com.oito.auth.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthProvider {
	FACEBOOK("facebook"), GOOGLE("google"), LINE("line"), LOCAL("local");

	private String provider;

}