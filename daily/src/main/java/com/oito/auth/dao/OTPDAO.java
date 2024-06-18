package com.oito.auth.dao;

import java.time.Instant;
import java.util.Optional;

import com.oito.auth.data.OTP;

public interface OTPDAO {
	OTP save(OTP otp);

	Optional<OTP> getById(long id);

	int updateOtpDetails(Long otpId, Instant expiresAt);

	Optional<OTP> findByPhoneNoAndExpiresAt(String phoneNo);

	Optional<OTP> findByEmailAndExpiresAt(String email);

	Optional<OTP> findByOtpIdAndPhoneNoAndExpiresAt(Long otpId, String phoneNo);

	Optional<OTP> findByOtpIdAndEmailAndExpiresAt(Long otpId, String email);

}
