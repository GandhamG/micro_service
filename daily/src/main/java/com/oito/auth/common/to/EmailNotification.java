package com.oito.auth.common.to;

import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class EmailNotification extends Notification {
	private String subject;

	public EmailNotification(final String template, final String locale, final List<String> to,
			final Map<String, Object> params, final String subject) {
		super(NotificationType.EMAIL, template, params, locale, to);
		this.subject = subject;
	}
}
