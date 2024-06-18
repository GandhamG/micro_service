package com.oito.auth.persistence;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.oito.auth.data.User;

public interface CriteriaGenerator {
	Predicate getPredicate(Root<User> root, CriteriaBuilder criteriaBuilder);
}
