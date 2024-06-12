package com.oito.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/external")
public class AuthExternalController {

	@Value("${domain.name}")
	private String domainName;

	@PostMapping("facebook/data-delete")
	public Map<String, Object> faceBookDataDeletionRequestCallBack(
			@SuppressWarnings("unused") @RequestBody final Map<String, String> requestBody) {
		return Map.of("url", domainName, "confirmation_code", "SUCCESS");
	}

}
