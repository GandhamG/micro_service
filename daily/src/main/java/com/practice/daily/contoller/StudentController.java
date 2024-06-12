package com.practice.daily.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.daily.service.StudentService;
import com.practice.daily.vo.StudentVO;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;

	@PostMapping("/save")
	public Object save(@RequestBody final StudentVO student) {
		return studentService.save(student);
	}

	@PutMapping
	public Object update(@RequestBody final StudentVO student) {
		return studentService.update(student);
	}

}
