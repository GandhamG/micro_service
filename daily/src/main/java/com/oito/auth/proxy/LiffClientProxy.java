package com.oito.auth.proxy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.oito.auth.config.feign.CoreFeignConfiguration;

@FeignClient(name = "liffClient", url = "https://api.line.me", configuration = CoreFeignConfiguration.class)
public interface LiffClientProxy {

	@PostMapping(value = "oauth2/v2.1/verify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	Map<String, Object> verifyIdToken(@RequestBody Map<String, ?> form);

}