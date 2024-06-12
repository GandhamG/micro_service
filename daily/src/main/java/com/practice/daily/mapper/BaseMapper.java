package com.practice.daily.mapper;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;

/**
 * @param <T> transferrable object here it is VO
 * @param <E> entity object
 */
public interface BaseMapper<T, E> {

	T toVO(E entity);

	List<T> toVOList(List<E> entityList);

	@InheritInverseConfiguration(name = "toVO")
	E toEntity(T vo);

	List<E> toEntityList(List<T> voList);

}
