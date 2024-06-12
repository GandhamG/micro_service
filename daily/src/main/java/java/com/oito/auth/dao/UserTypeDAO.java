package com.oito.auth.dao;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.UserTypeTO;

public interface UserTypeDAO {
	UserTypeTO findById(Long id);

	UserTypeTO findByUserIdAndType(Long userId, AuthUserType userType);

	UserTypeTO save(UserTypeTO usertype);

	int deleteById(Long id);

}
