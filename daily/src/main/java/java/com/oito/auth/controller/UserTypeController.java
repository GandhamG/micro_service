package com.oito.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserTypeRequest;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("user")
public class UserTypeController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserControllerHelper userControllerHelper;

	@PutMapping("type")
	public UserTypeTO updateUserType(@RequestBody final UserTypeRequest userTypeRequest) {
		try {
			return userService.updateUserType(userTypeRequest);
		} catch (final AuthException e) {
			log.error("AuthException updateUserType Occured", e);
			return new UserTypeTO(e.getAuthErrorCode(), e.getAuthErrorCode().getErrorMessage());
		} catch (final Exception e) {
			log.error("Exception updateUserType Occured", e);
			return new UserTypeTO(AuthErrorCode.UNKNOWN_EXCEPTION, AuthErrorCode.UNKNOWN_EXCEPTION.getErrorMessage());
		}
	}

	@DeleteMapping("type/secondary-user")
	public UserTypeTO deleteTypeUserMetadata(@RequestBody final UserTypeRequest userTypeRequest) {
		return userService.deleteSecondaryUser(userTypeRequest);
	}

	@PostMapping("user-type")
	public AppUserTO addUserType(@RequestBody final UserTypeRequest userTypeRequest) {
		try {
			log.info("User type request {}", userTypeRequest);
			return userService.addUserType(userTypeRequest);
		} catch (final AuthException e) {
			log.error("AuthException addUserType Occured", e);
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			log.error("Exception addUserType Occured", e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@PostMapping("type")
	@ResponseBody
	public AppUserTO addUserTypeSecure(@RequestBody final UserTypeRequest request) {
		try {
			log.info("Add user type request {}", request);
			return userService.addUserTypeSecure(request);
		} catch (final AuthException e) {
			log.error("AuthException addUserTypeSecure Occured", e);
			return userControllerHelper.formErrorTO(e);
		} catch (final Exception e) {
			log.error("Exception addUserTypeSecure Occured", e);
			return userControllerHelper.formErrorTO(e, AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

}
