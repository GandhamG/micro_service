package com.oito.auth.data;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(CustomAuditListener.class)
@Table(name = "OTP")
@Getter
@Setter
@NoArgsConstructor
public class OTP implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "otp_id")
	private Long otpId;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "send_count")
	private Integer sendCount;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "email")
	private String email;

	@Column(name = "expires_at")
	private Instant expiresAt;

	@Column(name = "otp_token")
	private String otpToken;

	@Embedded
	private Audit audit;

	public OTP(final Long userId, final Integer sendCount, final String otpToken, final Instant expiresAt,
			final String phoneNo, final String email) {
		this.userId = userId;
		this.sendCount = sendCount;
		this.otpToken = otpToken;
		this.expiresAt = expiresAt;
		this.phoneNo = phoneNo;
		this.email = email;

	}
}
