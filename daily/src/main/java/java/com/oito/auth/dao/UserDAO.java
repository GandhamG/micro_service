package com.oito.auth.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangeEmailRequest;
import com.oito.auth.common.to.ListResponse;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.UserIdentifierSearchRequest;
import com.oito.auth.common.to.UserListRequest;
import com.oito.auth.common.to.UserLoginAttemptVO;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.data.User;
import com.oito.auth.data.UserType;

public interface UserDAO {

	List<AppUserTO> getUsersByEmails(final List<String> emailList);

	List<AppUserTO> getUsersByPhoneNos(final List<String> phoneNos);

	Optional<AppUserTO> findByClientId(String clientId);

	Optional<AppUserTO> getUserByPhone(String phone);

	Optional<AppUserTO> findById(Long id);

	Map<Long, AppUserTO> getUsersByUserIds(final List<Long> userIds);

	Optional<AppUserTO> getUsersByUserEmail(String userEmail);

	Optional<AppUserTO> getUsersByUserName(String userName);

	List<AppUserTO> getUsersByUserNameOrEmailOrPhone(String userName, String userEmail, String phone);

	void save(User user);

	Long countByUseremail(String userEmail);

	Optional<AppUserTO> findBySocialId(String socialId, AuthProvider provider);

	void updateSocialInfo(AppUserTO userTO, SocialLoginRequest loginRequest);

	UserTypeTO updateUserType(UserType userType);

	ListResponse<AppUserTO> searchUser(UserListRequest userListRequest);

	void changeEmailHistory(ChangeEmailRequest changeEmailRequest);

	List<AppUserTO> findAll();

	int updateLastAccessToken(Long userId, String currentToken, String newToken);

	void bulkUpdateForUserDelete(final List<Long> userIds, final UserStatus status);

	Long countByPhoneNo(String phoneNo);

	Long searchUserCount(UserListRequest userListRequest);

	List<AppUserTO> getByUserIdentifierList(UserIdentifierSearchRequest request);

	Long countByPhoneNoAndUserType(String phoneNo, AuthUserType userType);

	void updateLineSocial(Long userId, String lineId);

	void updateCommunicationChannel(Long userId, Set<CommunicationChannel> communicationChannels);

	Optional<AppUserTO> findByUserEmailAndType(String userEmail, AuthUserType type);

	List<AppUserTO> filterUserByMetadata(String code, String value);

	List<AppUserTO> findUserByPrivilege(String privilegeCode, String accessCode);

	UserLoginAttemptVO executeLoginAttempts(UserLoginAttemptVO loginAttemptVO);

}
