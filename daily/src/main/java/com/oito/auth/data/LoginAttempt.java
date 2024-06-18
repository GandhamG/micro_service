package com.oito.auth.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "login_attempts")
@Getter
@Setter
public class LoginAttempt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "email")
	private String useremail;

	@Column(name = "phone_no")
	private String phoneNo;

	@Column(name = "attempts")
	private Integer attempts = Integer.valueOf(1);

	@Column(name = "last_modified")
	private LocalDateTime lastModified;

}
