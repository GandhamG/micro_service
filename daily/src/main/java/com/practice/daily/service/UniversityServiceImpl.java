package com.practice.daily.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practice.daily.repo.UniversityRepo;
import com.practice.daily.vo.UniversityVO;
import com.practice.daily.vo.mapper.StudentMapper;
import com.practice.daily.vo.mapper.UniversityMapper;

@Service
public class UniversityServiceImpl implements UniversityService {

	@Autowired
	private UniversityRepo uRepo;

	@Autowired
	private UniversityMapper uMapper;

	@Autowired
	private StudentMapper sMappeer;

	@Override
	public Object save(final UniversityVO vo) {
		final var university = uMapper.toEntity(vo);
		return uRepo.save(university);
	}

	@Override
	public Object getAll() {
		uRepo.deleteById(1L);
		return null;
	}

	@Override
	public Object update(final UniversityVO vo) {
		final var university = uRepo.findById(1L).orElse(null);
		if (university != null) {
			final var student = sMappeer.toEntity(vo.getStudentVO().get(0));
			university.getStudent().add(student);
			return uRepo.save(university);
		}
		return university;

	}

}
