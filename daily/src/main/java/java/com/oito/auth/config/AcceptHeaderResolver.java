package com.oito.auth.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

public class AcceptHeaderResolver extends AcceptHeaderLocaleResolver {

	private static final List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("th"), new Locale("id"));

	@Override
	public Locale resolveLocale(final HttpServletRequest request) {
		final var headerLang = request.getHeader("Accept-Language");
		return headerLang == null || headerLang.isEmpty() ? Locale.getDefault()
				: Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
	}

}