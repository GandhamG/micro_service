package com.oito.auth.proxy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleClient", url = "https://www.googleapis.com")
public interface GoogleProxy {

	@GetMapping(value = "/userinfo/v2/me")
	Map<String, Object> getProfile(@RequestParam("access_token") String accessToken);

}