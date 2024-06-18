package com.oito.auth.dao;

import com.oito.auth.common.UserTokenType;
import com.oito.auth.common.to.UserTokenTO;

public interface UserTokenDAO {

	UserTokenTO save(UserTokenTO usertype);

	UserTokenTO findByToken(String token);

	UserTokenTO findUserTokenByTypeAndMacIdAndUserTypeId(UserTokenType tokenType, String macId, Long userTypeId);

}
