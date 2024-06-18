/**
 *
 */
package com.oito.auth.data;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.dao.audit.Auditable;
import com.oito.auth.dao.audit.CustomAuditListener;
import com.oito.auth.jpa.CommunicationChannelConverter;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * User entity representation of database user model
 *
 * @author Dileep
 *
 */
@Entity
@EntityListeners(CustomAuditListener.class)
@Table(name = "Users", uniqueConstraints = @UniqueConstraint(columnNames = { "useremail" }))
@Getter
@Setter
public class User implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	@JsonIgnore
	@Audited
	private Long userId;

	private String orgIdfier;

	@Audited
	private String useremail;

	@Audited
	@Column(name = "username")
	private String userName;

	@JsonFormat(shape = JsonFormat.Shape.OBJECT)
	@Column(name = "source", columnDefinition = "JSON")
	private String source;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private UserStatus status = UserStatus.ACTIVE;

	@Audited
	@Column(name = "user_password")
	private String userSecretHash;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "client_secret")
	private String clientSecretHash;

	private String userBusinessType;

	@Column(name = "full_name")
	private String fullName;

	@Audited
	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "phone_no_verified")
	private Boolean phoneVerified = Boolean.FALSE;

	@Column(name = "reset_token")
	private String resetToken;

	@Column(name = "social_flag")
	private Boolean socialFlag;

	@Column(name = "facebook_id")
	private String facebookId;

	@Column(name = "google_id")
	private String googleId;

	@Column(name = "line_id")
	private String lineId;

	@Column(name = "last_access_token1")
	private String lastAccessToken1;

	@Column(name = "last_access_token2")
	private String lastAccessToken2;

	@Column(name = "stay_signed_in")
	private Boolean staySignedIn = Boolean.FALSE;

	@Audited
	@Column(name = "phone_country_code")
	private String phoneCountryCode;

	@Column(name = "verified")
	private Boolean verified = Boolean.FALSE;

	private String address;

	private String personalizationId;

	private String preferredLanguage = "en";

	private LocalDateTime registeredTime;

	private LocalDateTime lastSession;

	@Column(name = "last_login_timestamp")
	private LocalDateTime lastLoginTimestamp;

	@Column(name = "token_expiry_timestamp")
	private LocalDateTime tokenExpiryTimestamp;

	@Audited
	@Convert(converter = CommunicationChannelConverter.class)
	@Column(name = "communication_channels")
	private Set<CommunicationChannel> communicationChannels;

	@Embedded
	private Audit audit;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private Set<UserType> userTypes;

}
