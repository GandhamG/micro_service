/**
 *
 */
package com.oito.auth.common.to;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * User data transfer object
 *
 * @author Dileep
 *
 */
@Getter
@Setter
@ToString
public class UserDeleteRequestDTO {

	private Long userId;

	private Long id;

	private UserDeleteRequestStatus status;

	private LocalDateTime processedTimestamp;

	private String reason;

	private AuditVO audit;
}
