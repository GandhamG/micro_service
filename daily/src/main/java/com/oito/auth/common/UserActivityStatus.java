package com.oito.auth.common;

import java.time.LocalDateTime;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.oito.auth.data.User;
import com.oito.auth.persistence.CriteriaGenerator;

public enum UserActivityStatus implements CriteriaGenerator {
	ACTIVE {
		@Override
		public Predicate getPredicate(final Root<User> root, final CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get(Constants.LAST_LOGIN_TIME_FIELD),
					LocalDateTime.now().minusDays(Constants.ACTIVE_DAYS)));
		}
	},
	INACTIVE {
		@Override
		public Predicate getPredicate(final Root<User> root, final CriteriaBuilder criteriaBuilder) {
			return ACTIVE.getPredicate(root, criteriaBuilder).not();
		}
	},
	VERIFIED {
		@Override
		public Predicate getPredicate(final Root<User> root, final CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.and(criteriaBuilder.equal(root.get(Constants.VERIFIED_FIELD), Boolean.TRUE));
		}
	},
	UNVERIFIED {
		@Override
		public Predicate getPredicate(final Root<User> root, final CriteriaBuilder criteriaBuilder) {
			return VERIFIED.getPredicate(root, criteriaBuilder).not();
		}
	};

}
