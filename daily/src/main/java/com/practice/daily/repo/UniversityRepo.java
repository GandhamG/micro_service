package com.practice.daily.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.practice.daily.entity.University;

public interface UniversityRepo extends JpaRepository<University, Long> {

}
