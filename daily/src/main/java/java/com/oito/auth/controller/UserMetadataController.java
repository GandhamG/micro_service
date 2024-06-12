package com.oito.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.Status;
import com.oito.auth.common.to.UserMetadataRequest;
import com.oito.auth.common.to.UserMetadataTO;
import com.oito.auth.common.to.UserMetadataUpdateRequest;
import com.oito.auth.service.UserMetadataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user/metadata")
@Slf4j
public class UserMetadataController {

	@Autowired
	private UserMetadataService userMetadataService;

	@PostMapping
	public List<UserMetadataTO> create(@RequestBody final UserMetadataRequest userMetadataRequest) {
		log.info("Inside userMetadata Save {}", userMetadataRequest);
		return userMetadataService.save(userMetadataRequest.getCustomFields(), userMetadataRequest.getUserId());
	}

	@PutMapping
	public SimpleResponse update(@RequestBody final UserMetadataUpdateRequest userMetadataUpdateRequest) {
		userMetadataService.updateByCode(userMetadataUpdateRequest);
		return SimpleResponse.success();
	}

	@PutMapping("sms")
	public ResponseEntity<Status> update(@RequestBody final UserMetadataRequest userMetadataRequest) {
		userMetadataService.merge(userMetadataRequest.getCustomFields(), userMetadataRequest.getUserId());
		return ResponseEntity.ok(Status.SUCCESS);

	}

	@GetMapping("{code}/{value}")
	public List<AppUserTO> filterUserByMetadata(@PathVariable final String code, @PathVariable final String value) {
		return userMetadataService.filterUserByMetadata(code, value);
	}
}
