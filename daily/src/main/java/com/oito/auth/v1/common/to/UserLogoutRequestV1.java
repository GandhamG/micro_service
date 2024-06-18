/**
 *
 */
package com.oito.auth.v1.common.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author krishna
 *
 */
@Getter
@Setter
@ApiModel(value = "UserLogoutRequestV1", description = "This represents the User Logout Request version1")
@ToString
public class UserLogoutRequestV1 {
	@ApiModelProperty(notes = "Device Id", example = "2c549188c9e3")
	private String deviceId;
}
