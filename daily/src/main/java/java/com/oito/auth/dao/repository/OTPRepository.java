package com.oito.auth.dao.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.oito.auth.data.OTP;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {
	@Modifying(clearAutomatically = true)
	@Query("update OTP set sendCount=sendCount+1, expiresAt=:expiresAt where otpId=:otpId")
	int updateOtpDetails(@Param("otpId") Long otpId, @Param("expiresAt") Instant expiresAt);

	@Query("select o from OTP o where o.phoneNo=:phoneNo and o.expiresAt>=now()")
	Optional<OTP> findByPhoneNoAndExpiresAt(@Param("phoneNo") String phoneNo);

	@Query("select o from OTP o where o.email=:email and o.expiresAt>=now()")
	Optional<OTP> findByEmailAndExpiresAt(@Param("email") String email);

	@Query("select o from OTP o where o.otpId=:otpId and o.phoneNo=:phoneNo and (o.expiresAt is null or o.expiresAt>=now())")
	Optional<OTP> findByOtpIdAndPhoneNoAndExpiresAt(@Param("otpId") Long otpId, @Param("phoneNo") String phoneNo);

	@Query("select o from OTP o where o.otpId=:otpId and o.email=:email and (o.expiresAt is null or o.expiresAt>=now())")
	Optional<OTP> findByOtpIdAndEmailAndExpiresAt(@Param("otpId") Long otpId, @Param("email") String email);

}
