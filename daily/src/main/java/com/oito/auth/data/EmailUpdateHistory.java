/**
 *
 */
package com.oito.auth.data;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 *
 * @author Jobin
 *
 */
@Entity
@Table(name = "email_update_history")
@Getter
@Setter
@NoArgsConstructor
public class EmailUpdateHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long historyId;

	@Column(nullable = false)
	private String fromEmail;

	@Column(nullable = false)
	private String toEmail;

	@Column(nullable = false)
	private Long createdBy;

	@Column(nullable = false)
	private LocalDateTime createdTimestamp;

}
