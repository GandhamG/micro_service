package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.ClientTO;
import com.oito.auth.common.to.ClientUserLoginRequest;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface ClientUserLoginRequesttoClientTOMapper extends BaseMapper<ClientTO, ClientUserLoginRequest> {

}
