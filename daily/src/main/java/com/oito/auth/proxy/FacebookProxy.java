package com.oito.auth.proxy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facebookClient", url = "https://graph.facebook.com")
public interface FacebookProxy {

	@GetMapping(value = "/v11.0/me")
	Map<String, Object> getProfile(@RequestParam("fields") String fields,
			@RequestParam("access_token") String accessToken);

}