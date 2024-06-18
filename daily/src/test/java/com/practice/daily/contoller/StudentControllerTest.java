package com.practice.daily.contoller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.practice.daily.repo.StudentRepository;
import com.practice.daily.service.StudentService;
import com.practice.daily.vo.mapper.StudentMapper;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

	@MockBean
	private StudentService studentService;

	@MockBean
	private StudentRepository repo;

	@MockBean
	private StudentMapper studentMapper;

}
