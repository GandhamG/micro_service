package com.oito.auth.common.to;

import java.util.List;
import java.util.Map;

public class SMSNotification extends Notification {

	public SMSNotification(final String template, final String locale, final String phone, final List<String> args) {
		super(NotificationType.SMS, template, Map.of("params", args), locale, List.of(phone));
	}
}
