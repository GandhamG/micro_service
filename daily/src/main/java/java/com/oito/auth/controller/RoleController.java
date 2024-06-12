package com.oito.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.common.to.RoleRequest;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.service.RoleService;

@RestController
@RequestMapping("/user/role")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@GetMapping
	public List<RoleDTO> roles() {
		return roleService.getRoles();
	}

	@GetMapping("/{userId}")
	public List<RoleDTO> userRoles(@PathVariable final Long userId) {
		return roleService.getUserRoles(userId);
	}

	@PostMapping
	public List<RoleDTO> addUserRoles(@RequestBody final RoleRequest request) {
		return roleService.addUserRoles(request);
	}

	@PatchMapping
	public SimpleResponse deleteUserRoles(@RequestBody final RoleRequest request) {
		roleService.deleteUserRoles(request);
		return SimpleResponse.success();
	}

}
