/**
 *
 */
package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.mapper.AuditMapper;
import com.oito.auth.mapper.BaseMapper;
import com.oito.auth.v1.common.to.UserSignUpRequestV1;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserSignUpRequestMapperV1 extends BaseMapper<UserSignUpRequestV1, UserSignUpRequest> {

}
