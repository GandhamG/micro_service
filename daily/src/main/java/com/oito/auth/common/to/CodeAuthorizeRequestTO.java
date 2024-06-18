/**
 *
 */
package com.oito.auth.common.to;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * User data transfer object
 *
 * @author Dileep
 *
 */
@Getter
@Setter
public class CodeAuthorizeRequestTO {

	private String code;

	private String callbackUri;
}
