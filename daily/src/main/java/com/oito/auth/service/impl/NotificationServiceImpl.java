package com.oito.auth.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.oito.ResetPasswordApplication.BulkResetPasswordRequest;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.EmailNotification;
import com.oito.auth.common.to.Notification;
import com.oito.auth.common.to.Notification.NotificationType;
import com.oito.auth.common.to.SMSNotification;
import com.oito.auth.proxy.NotificationServiceProxy;
import com.oito.auth.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	public NotificationServiceProxy notificationProxy;

	@Autowired
	private MessageSource messageSource;

	@Override
	public void notifyResetPassword(final BulkResetPasswordRequest request, final NotificationType type,
			final AppUserTO userInfo, final String resetLink) {
		final var notification = buildResetPasswordNotificationRequest(request, type, userInfo, resetLink);
		notificationProxy.notify(notification, request.getUserContext());
	}

	private Notification buildResetPasswordNotificationRequest(final BulkResetPasswordRequest request,
			final NotificationType type, final AppUserTO userInfo, final String resetLink) {
		if (NotificationType.SMS.equals(type)) {
			return new SMSNotification(request.getSmsTemplate(), request.getLocale(), userInfo.getPhoneNo(),
					List.of(userInfo.getFullName(), resetLink));
		}
		final Map<String, Object> emailParams = new HashMap<>();
		emailParams.put("name", userInfo.getFullName());
		emailParams.put("buyerName", userInfo.getFullName());
		emailParams.put("resetPasswordLink", resetLink);
		emailParams.put("verifyLink", resetLink);
		return new EmailNotification(request.getEmailTemplate(), request.getLocale(), List.of(userInfo.getUseremail()),
				emailParams, messageSource.getMessage(request.getEmailSubjectKey(), new Object[] {},
						request.getEmailSubjectKey(), new Locale(request.getLocale())));
	}
}
