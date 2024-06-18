package com.oito.auth.dao;

import java.time.Instant;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.dao.repository.OTPRepository;
import com.oito.auth.data.OTP;

@Component
public class OTPDAOImpl implements OTPDAO {

	@Autowired
	private OTPRepository repo;

	@Override
	public OTP save(final OTP otp) {
		return repo.save(otp);
	}

	@Override
	public int updateOtpDetails(final Long otpId, final Instant expiresAt) {
		return repo.updateOtpDetails(otpId, expiresAt);
	}

	@Override
	public Optional<OTP> findByPhoneNoAndExpiresAt(final String phoneNo) {
		return Optional.ofNullable(phoneNo).filter(StringUtils::isNotBlank).flatMap(repo::findByPhoneNoAndExpiresAt);
	}

	@Override
	public Optional<OTP> findByEmailAndExpiresAt(final String email) {
		return Optional.ofNullable(email).filter(StringUtils::isNotBlank).flatMap(repo::findByEmailAndExpiresAt);
	}

	@Override
	public Optional<OTP> getById(final long id) {
		return repo.findById(Long.valueOf(id));
	}

	@Override
	public Optional<OTP> findByOtpIdAndPhoneNoAndExpiresAt(final Long otpId, final String phoneNo) {
		return Optional.ofNullable(phoneNo).filter(StringUtils::isNotBlank)
				.flatMap(otp -> repo.findByOtpIdAndPhoneNoAndExpiresAt(otpId, phoneNo));
	}

	@Override
	public Optional<OTP> findByOtpIdAndEmailAndExpiresAt(final Long otpId, final String email) {
		return Optional.ofNullable(email).filter(StringUtils::isNotBlank)
				.flatMap(otp -> repo.findByOtpIdAndEmailAndExpiresAt(otpId, email));
	}

}
