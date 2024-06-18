package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserPrivilegeAssignmentDTO;
import com.oito.auth.data.UserPrivilegeAssignment;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPrivilegeAssignmentMapper extends BaseMapper<UserPrivilegeAssignmentDTO, UserPrivilegeAssignment> {

}
