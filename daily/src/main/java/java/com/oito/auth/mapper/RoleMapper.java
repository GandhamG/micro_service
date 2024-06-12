package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.RoleDTO;
import com.oito.auth.data.Role;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper extends BaseMapper<RoleDTO, Role> {

}
