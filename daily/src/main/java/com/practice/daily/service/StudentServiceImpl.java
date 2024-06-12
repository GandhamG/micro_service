package com.practice.daily.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.practice.daily.repo.StudentRepository;
import com.practice.daily.vo.StudentVO;
import com.practice.daily.vo.mapper.StudentMapper;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentRepository repo;

	@Autowired
	private StudentMapper studentMapper;

	@Override
	public Object save(final StudentVO student) {
		final var stu = studentMapper.toEntity(student);
		return repo.save(stu);
	}

	@Override
	public Object update(final StudentVO student) {
		final var stu = repo.findById(student.getStudentId()).get();
		studentMapper.toEntity(student, stu);
		return repo.save(stu);
	}

}
