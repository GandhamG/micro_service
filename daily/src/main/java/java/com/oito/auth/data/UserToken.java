package com.oito.auth.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oito.auth.common.UserTokenType;
import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_token")
@EntityListeners(CustomAuditListener.class)
@Getter
@Setter
@NoArgsConstructor
public class UserToken implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_token_id")
	private Long userTokenId;

	@Column(name = "user_type_id")
	private Long userTypeId;

	@Column(name = "token")
	private String token;

	@Column(name = "token_type")
	@Enumerated(EnumType.STRING)
	private UserTokenType tokenType;

	@Column(name = "mac_id")
	private String macId;

	@Column(name = "expiry_timestamp")
	private LocalDateTime expiryTimeStamp;

	@Embedded
	private Audit audit;

}
