package com.oito.auth.common.to;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Notification {

	public enum NotificationType {
		EMAIL, SMS;
	}

	private NotificationType type;

	private String template;

	private Map<String, Object> params;

	private String locale;

	private List<String> to;
}
