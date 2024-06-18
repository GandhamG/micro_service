package com.oito.auth.otp;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.oito.common.util.Booleans;
import com.oito.common.util.Nulls;
import com.oito.auth.common.Constants;
import com.oito.auth.common.UserTokenType;
import com.oito.auth.common.to.EmailNotification;
import com.oito.auth.common.to.OTPRequest;
import com.oito.auth.common.to.SMSNotification;
import com.oito.auth.dao.OTPDAO;
import com.oito.auth.data.OTP;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.proxy.NotificationServiceProxy;
import com.oito.common.auth.jwt.JWETokenHandler;
import com.oito.common.exception.ApiException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OTPHandler {

	private final Random random = new Random();

	private static final int OTP_SIZE = 6;

	@Value("${oito.otp.max-send-count:5}")
	private int maxSendCount;

	private static final String OTP_CHARACTERS = "1234567890";

	private static final String OTP_EMAIL_SUBJECT = "otp.email.subject";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private OTPDAO dao;

	@Autowired
	private NotificationServiceProxy notificationProxy;

	@Value("${oito.otp.expiry-in-minutes}")
	private long expiryInMinutes;

	@Autowired
	private JWETokenHandler jweTokenHandler;

	/**
	 * @param user
	 * @param locale
	 * @param otpId
	 * @return
	 */

	public Long generate(final Long userId, final String phoneNumber, final String email, final OTPRequest request) {
		// 1. Generate OTP
		final var otpCode = createOTP();
		// 2. Save
		final var otp = otpRecord(userId, request.getOtpId(), otpCode, phoneNumber, email);
		dao.save(otp);
		// 3. Send
		notifyOTP(phoneNumber, email, request, otpCode);
		return otp.getOtpId();
	}

	private String generateOTPToken(final String email, final String phoneNumber, final String otpCode,
			final Instant expirtyTime) {
		final var claimsMap = new HashMap<String, String>(3);
		claimsMap.put(Constants.PHONE_FIELD, phoneNumber);
		Booleans.ifTrue(StringUtils.isNotBlank(email), () -> claimsMap.put(Constants.EMAIL_FIELD, email));
		claimsMap.put(Constants.OTP_FIELD, otpCode);
		claimsMap.put(Constants.TOKEN_TYPE_KEY, UserTokenType.OTP.name());
		return jweTokenHandler.generateJWT(claimsMap, List.of(), expirtyTime, otpCode);
	}

	private void notifyOTP(final String phoneNumber, final String email, final OTPRequest request,
			final String otpCode) {
		if (StringUtils.isNotBlank(phoneNumber)) {
			notificationProxy.notify(
					new SMSNotification(request.getOtpTemplate(), request.getLocale(), phoneNumber, List.of(otpCode)));
		}
		if (StringUtils.isNotBlank(email)) {
			final Map<String, Object> emailParams = new HashMap<>();
			if (request.getUserParams() != null) {
				emailParams.putAll(request.getUserParams());
			}
			emailParams.put("otp", otpCode);
			final var subject = request.getEmailSubject() == null
					? messageSource.getMessage(OTP_EMAIL_SUBJECT, new Object[] {}, new Locale(request.getLocale()))
					: request.getEmailSubject();
			notificationProxy.notify(new EmailNotification(request.getEmailTemplate(), request.getLocale(),
					List.of(email), emailParams, subject));
		}
	}

	public void resend(final String phoneNumber, final String email, final OTPRequest request) {
		final var otp = dao.findByOtpIdAndPhoneNoAndExpiresAt(request.getOtpId(), phoneNumber)
				.or(() -> dao.findByOtpIdAndEmailAndExpiresAt(request.getOtpId(), email))
				.orElseThrow(() -> new AuthException(AuthErrorCode.OTP_NOT_FOUND_OR_EXPIRED));

		final var otpCode = getOTPToken(otp).get(Constants.OTP_FIELD);
		if (StringUtils.isBlank(otpCode)) {
			throw new AuthException(AuthErrorCode.OTP_NOT_FOUND_OR_EXPIRED);
		}
		updateOtpRecord(otpCode, phoneNumber, email, otp, Instant.now().plus(expiryInMinutes, ChronoUnit.MINUTES));

		dao.save(otp);
		notifyOTP(phoneNumber, email, request, otpCode);
	}

	private OTP otpRecord(final Long userId, final Long otpId, final String otpCode, final String phoneNo,
			final String email) {
		OTP otp;
		final var otpExpiry = Instant.now().plus(expiryInMinutes, ChronoUnit.MINUTES);
		if (Objects.isNull(otpId)) {
			otp = dao.findByPhoneNoAndExpiresAt(phoneNo).or(() -> dao.findByEmailAndExpiresAt(email)).orElse(null);
			if (otp == null) {
				return (new OTP(userId, NumberUtils.INTEGER_ONE, generateOTPToken(email, phoneNo, otpCode, otpExpiry),
						otpExpiry, phoneNo, email));
			}
			return updateOtpRecord(otpCode, phoneNo, email, otp, otpExpiry);
		}
		otp = dao.findByOtpIdAndPhoneNoAndExpiresAt(otpId, phoneNo)
				.or(() -> dao.findByOtpIdAndEmailAndExpiresAt(otpId, email))
				.orElseThrow(() -> new AuthException(AuthErrorCode.OTP_NOT_FOUND_OR_EXPIRED));
		return updateOtpRecord(otpCode, phoneNo, email, otp, otpExpiry);
	}

	private OTP updateOtpRecord(final String otpCode, final String phoneNo, final String email, final OTP otp,
			final Instant otpExpiry) {
		ApiException.ifThrow(otp.getSendCount() >= maxSendCount, AuthErrorCode.OTP_RESEND_LIMIT_EXCEEDED);
		otp.setOtpToken(generateOTPToken(email, phoneNo, otpCode, otpExpiry));
		otp.setSendCount(Integer.valueOf(otp.getSendCount().intValue() + 1));
		otp.setExpiresAt(otpExpiry);
		return otp;
	}

	public boolean verifyOTP(final long otpId, final String otpCode) {
		return otpCode.equals(getOTPToken(otpId).get(Constants.OTP_FIELD));
	}

	public boolean verifyOTP(final long otpId, final String otpCode, final String phone) {
		final var tokenMap = getOTPToken(otpId);
		if (MapUtils.isNotEmpty(tokenMap)) {
			return tokenMap.get(Constants.OTP_FIELD).equals(otpCode)
					&& tokenMap.get(Constants.PHONE_FIELD).equals(phone)
					&& UserTokenType.OTP.name().equals(tokenMap.get(Constants.TOKEN_TYPE_KEY));
		}
		return false;
	}

	private Map<String, String> getOTPToken(final long otpId) {
		return dao.getById(otpId).map(this::getOTPToken).orElseGet(Map::of);
	}

	private Map<String, String> getOTPToken(final OTP otp) {
		try {
			return Nulls.getIfNonNull(otp.getOtpToken(), Map.of(),
					() -> jweTokenHandler.decodeToken(otp.getOtpToken()));
		} catch (final ApiException e) {
			log.info("OTP Token Expired returning empty map {}", e.getMessage());
			return Map.of();
		}
	}

	private String createOTP() {
		final var otp = new char[OTP_SIZE];
		for (var i = 0; i < OTP_SIZE; i++) {
			otp[i] = OTP_CHARACTERS.charAt(random.nextInt(OTP_CHARACTERS.length()));
		}
		return new String(otp);
	}

	public Optional<String> findRecentOtp(final String phoneNo, final String email) {
		log.info("Loading data OTP from dB " + phoneNo + " - " + email);
		return dao.findByPhoneNoAndExpiresAt(phoneNo).or(() -> dao.findByEmailAndExpiresAt(email))
				.map(this::getOTPToken).map(map -> map.get(Constants.OTP_FIELD));
	}

}
