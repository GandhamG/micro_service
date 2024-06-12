/**
 *
 */
package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.SignUpResponse;
import com.oito.auth.mapper.AuditMapper;
import com.oito.auth.mapper.BaseMapper;
import com.oito.auth.v1.common.to.SignUpResponseV1;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SignUpResponseMapperV1 extends BaseMapper<SignUpResponse, SignUpResponseV1> {

}
