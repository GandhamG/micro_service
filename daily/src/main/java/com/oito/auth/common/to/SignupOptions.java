/**
 *
 */
package com.oito.auth.common.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.oito.auth.common.AuthUserType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author krishna
 *
 */
@Getter
@Setter
@JsonIgnoreType
@AllArgsConstructor
public class SignupOptions {
	private List<AuthUserType> optionalEmailUserTypes = List.of();
}
