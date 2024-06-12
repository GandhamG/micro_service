/**
 *
 */
package com.oito.auth.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.oito.auth.common.AuthProvider;

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
@Table(name = "user_login_history")
@Getter
@Setter
public class UserLoginHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String sessionId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AuthProvider authProvider;

	@Column(nullable = false)
	private LocalDateTime loginTimestamp;

	private LocalDateTime logoutTimestamp;

}
