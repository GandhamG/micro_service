package com.oito.auth.service;

import com.oito.ResetPasswordApplication.BulkResetPasswordRequest;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.Notification.NotificationType;

public interface NotificationService {
	void notifyResetPassword(BulkResetPasswordRequest request, NotificationType type, AppUserTO userInfo,
			String resetLink);
}
