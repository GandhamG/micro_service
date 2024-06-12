package com.oito.auth.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.oito.auth.data.LoginAttempt;

public interface UserLoginAttemptRepository
		extends JpaRepository<LoginAttempt, Long>, JpaSpecificationExecutor<LoginAttempt> {

	Optional<LoginAttempt> findByUseremail(String useremail);

	Optional<LoginAttempt> findByPhoneNo(String phoneNo);

}
