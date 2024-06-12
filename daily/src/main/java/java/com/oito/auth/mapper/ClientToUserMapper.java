package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.ClientTO;
import com.oito.auth.data.User;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface ClientToUserMapper extends BaseMapper<ClientTO, User> {

}
