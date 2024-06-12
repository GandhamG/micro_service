/**
 *
 */
package com.oito.auth.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.data.User;

/**
 * @author Dileep
 *
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findByUseremail(String useremail);

	Optional<User> findByUserName(String userName);

	Optional<User> findByClientId(String clientId);

	Long countByUseremail(String userEmail);

	Long countByPhoneNo(String phoneNo);

	Optional<User> findByPhoneNo(String phoneNo);

	Optional<User> findByGoogleId(String id);

	Optional<User> findByFacebookId(String id);

	Optional<User> findByLineId(String id);

	@Query("select usr from User usr join UserType usrType on usr.userId = usrType.userId where usr.useremail=:email and usrType.type=:type")
	Optional<User> findByUseremailAndUserType(@Param("email") String useremail, @Param("type") AuthUserType userType);

	@Query("select usr from User usr join UserType usrType on usr.userId = usrType.userId where usr.userName=:userName and usrType.type=:type")
	Optional<User> findByUserNameAndUserType(@Param("userName") String userName, @Param("type") AuthUserType userType);

	@Query("select usr from User usr join UserType usrType on usr.userId = usrType.userId where usr.phoneNo=:phone and usrType.type=:type")
	List<User> findByPhoneNoAndUserType(@Param("phone") String phoneNo, @Param("type") AuthUserType userType);

	@Query("select count(usr) from User usr join UserType usrType on usr.userId = usrType.userId where usr.phoneNo=:phone and usrType.type=:type")
	Long countByPhoneNoAndUserType(@Param("phone") String phoneNo, @Param("type") AuthUserType userType);

	List<User> findByUseremailIn(List<String> emailList);

	List<User> findByPhoneNoIn(List<String> phoneNos);

	List<User> findByUserIdIn(List<Long> userIds);

	List<User> findByUserNameOrUseremailOrPhoneNo(String userName, String userEmail, String phoneNo);

	List<User> findByUserIdInOrUseremailInOrPhoneNoInOrLineIdIn(List<Long> userIds, List<String> useremails,
			List<String> phoneNos, List<String> phoneIds);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.googleId = :googleId,u.socialFlag=true, u.audit.lastUpdatedTimestamp=CURRENT_TIMESTAMP where u.userId = :userId")
	void updateGoogleSocial(@Param("userId") Long userId, @Param("googleId") String googleId);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.facebookId = :facebookId,u.socialFlag=true, u.audit.lastUpdatedTimestamp=CURRENT_TIMESTAMP where u.userId = :userId")
	void updateFacebookSocial(@Param("userId") Long userId, @Param("facebookId") String facebookId);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.lineId = :lineId, u.socialFlag=true, u.audit.lastUpdatedTimestamp=CURRENT_TIMESTAMP where u.userId = :userId")
	void updateLineSocial(@Param("userId") Long userId, @Param("lineId") String lineId);

	// Native query is used since updating communicationChannels as a Set is
	// throwing exception
	@Modifying(clearAutomatically = true)
	@Query(nativeQuery = true, value = "update users u set u.communication_channels = :communicationChannels, u.last_updated_timestamp=CURRENT_TIMESTAMP where u.user_id = :userId")
	void updateCommunicationChannelInfo(@Param("userId") Long userId,
			@Param("communicationChannels") String communicationChannels);

	@Modifying(clearAutomatically = true)
	@Query("update User u set u.lastAccessToken2=:currentToken, u.lastAccessToken1 = :newToken,u.audit.lastUpdatedTimestamp=CURRENT_TIMESTAMP where u.userId = :userId and u.lastAccessToken1 = :currentToken")
	int updateLastAccessToken(@Param("userId") Long userId, @Param("currentToken") String currentToken,
			@Param("newToken") String newToken);

	@Modifying
	@Query("update User u set u.fullName = userId,u.userName = userId,lineId = null, facebookId=null, googleId=null, userSecretHash =null, u.phoneNo=userId, u.status =:status,u.useremail=replace(useremail, SUBSTRING_INDEX(SUBSTRING_INDEX(useremail, '@', 1), '.', 1) ,userId) where u.userId IN  :userIds")
	void bulkUpdateForUserDelete(@Param("userIds") List<Long> userIds, @Param("status") UserStatus status);

	@Modifying
	@Query("update User u set  u.status =:status where u.userId IN  :userIds")
	void bulkUpdateForUserUnDelete(@Param("userIds") List<Long> userIds, @Param("status") UserStatus status);

	@Query("select usr from User usr join UserMetadata metaData on usr.userId = metaData.userId where metaData.code=:code and metaData.value=:value")
	List<User> filterUserByMetadata(@Param("code") String code, @Param("value") String value);

	@Query("select usr from User usr join UserPrivilegeAssignment upa on (usr.userId = upa.userId and upa.type='ROLE') join RolePrivilegeMapping rpm on (upa.typeId = rpm.roleId) join Privilege p on (p.privilegeId = rpm.privilegeId) where p.resourceCode=:resourceCode and p.accessCode=:accessCode")
	List<User> findUserByPrivilege(@Param("resourceCode") String resourceCode, @Param("accessCode") String accessCode);
}
