package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.PrivilegeDTO;
import com.oito.auth.data.Privilege;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PrivilegeMapper extends BaseMapper<PrivilegeDTO, Privilege> {

}
