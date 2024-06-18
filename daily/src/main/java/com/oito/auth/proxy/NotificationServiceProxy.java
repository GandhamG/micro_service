package com.oito.auth.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.oito.auth.common.to.Notification;

@FeignClient(name = "notificationService", url = "${microservice.url.notification-service}")
public interface NotificationServiceProxy {

	@PostMapping("/notification")
	void notify(@RequestBody Notification notification);

	@PostMapping("/notification")
	void notify(@RequestBody Notification notification, @RequestHeader("userContext") String userContext);

	@GetMapping("/notification/line/follower/{lineId}")
	boolean isLineBotFollower(@PathVariable(name = "lineId") final String lineId);
}
