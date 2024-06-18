package com.oito.auth.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AuditVO;
import com.oito.auth.data.Audit;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuditMapper extends BaseMapper<AuditVO, Audit> {

	@Override
	AuditVO toVO(Audit audit);

	@Override
	@InheritInverseConfiguration(name = "toVO")
	Audit toEntity(AuditVO auditVO);

	void toEntity(AuditVO auditVO, @MappingTarget Audit audit);

}
