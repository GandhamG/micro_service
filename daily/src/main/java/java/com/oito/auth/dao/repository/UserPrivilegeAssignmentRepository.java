package com.oito.auth.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oito.auth.data.UserPrivilegeAssignment;

public interface UserPrivilegeAssignmentRepository extends JpaRepository<UserPrivilegeAssignment, Long> {

}
