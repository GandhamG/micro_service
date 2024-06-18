/**
 *
 */
package com.oito.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.BulkUserDeleteInputRequest;
import com.oito.auth.common.to.UserBulkDeleteResponse;
import com.oito.auth.common.to.UserDeleteInputRequest;
import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.common.to.UserDeleteResponse;
import com.oito.auth.service.UserDeleteRequestService;

/**
 * User Service controller
 *
 *
 *
 */
@RestController
@RequestMapping("/user-delete-request")
public class UserDeleteController {

	@Autowired
	private UserDeleteRequestService userDeleteService;

	@PostMapping
	public UserDeleteResponse create(@RequestBody final UserDeleteInputRequest request,
			@RequestHeader(HttpHeaders.AUTHORIZATION) final String accessToken) {
		request.setAccessToken(accessToken);
		return userDeleteService.create(request);
	}

	@PostMapping("/bulk")
	public UserDeleteResponse bulkCreate(@RequestBody final BulkUserDeleteInputRequest request) {
		return userDeleteService.bulkCreate(request);
	}

	@PutMapping("/bulk-delete")
	public UserBulkDeleteResponse bulkDelete(@RequestBody final List<Long> userIds) {
		return userDeleteService.bulkDelete(userIds);
	}

	@PutMapping
	public UserDeleteResponse update(@RequestBody final UserDeleteInputRequest request) {
		return userDeleteService.update(request);
	}

	@PutMapping("/undo-delete/{userId}")
	public UserDeleteResponse undoDelete(@PathVariable("userId") final Long userId) {
		return userDeleteService.undoDelete(userId);
	}

	@PutMapping("/undo-bulk-delete")
	public UserBulkDeleteResponse undoBulkDelete(@RequestBody final List<Long> userIds) {
		return userDeleteService.undoBulkDelete(userIds);
	}

	@PutMapping("/rollback")
	public UserDeleteResponse rollback(@RequestBody final UserDeleteResponse response) {
		return userDeleteService.rollback(response);
	}

	@GetMapping
	public List<UserDeleteResponse> getUserDeleteRequestByStatus(
			@RequestParam("status") final UserDeleteRequestStatus status,
			@RequestParam("agingInterval") final Long agingInterval) {
		return userDeleteService.getUserDeleteRequestByStatus(status, agingInterval);
	}

}
