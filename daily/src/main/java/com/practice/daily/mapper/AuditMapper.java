package com.practice.daily.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.practice.daily.audit.Audit;
import com.practice.daily.audit.AuditVO;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuditMapper extends BaseMapper<AuditVO, Audit> {

	@Override
	AuditVO toVO(Audit audit);

	@Override
	@InheritInverseConfiguration(name = "toVO")
	Audit toEntity(AuditVO auditVO);

	void toEntity(AuditVO auditVO, @MappingTarget Audit audit);

}
