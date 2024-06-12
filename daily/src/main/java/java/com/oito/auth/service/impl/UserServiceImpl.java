package com.oito.auth.service.impl;

import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.oito.ResetPasswordApplication.BulkResetPasswordRequest;
import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.ErrorAction;
import com.oito.auth.common.ObjectValidator;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.enumeration.UserCustomField;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangeEmailRequest;
import com.oito.auth.common.to.ChangePasswordRequest;
import com.oito.auth.common.to.ClientTO;
import com.oito.auth.common.to.ListResponse;
import com.oito.auth.common.to.Notification.NotificationType;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.OTPRequest;
import com.oito.auth.common.to.OTPResponse;
import com.oito.auth.common.to.PrivilegeRequest;
import com.oito.auth.common.to.SignUpResponse;
import com.oito.auth.common.to.SimpleResponse;
import com.oito.auth.common.to.Status;
import com.oito.auth.common.to.UserIdentifierSearchRequest;
import com.oito.auth.common.to.UserListRequest;
import com.oito.auth.common.to.UserLoginAttemptVO;
import com.oito.auth.common.to.UserLoginHistoryBean;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.common.to.UserTypeRequest;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.dao.PrivilegeDAO;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.dao.UserTypeDAO;
import com.oito.auth.dao.repository.UserLoginHistoryRepository;
import com.oito.auth.dao.repository.UserRepository;
import com.oito.auth.data.User;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.jwt.TokenHandler;
import com.oito.auth.mapper.AppUserTOSignUpResponseMapper;
import com.oito.auth.mapper.AppUserToUserMapper;
import com.oito.auth.mapper.ClientToUserMapper;
import com.oito.auth.mapper.UserLoginHistoryMapper;
import com.oito.auth.mapper.UserSignUpRequestToUserMapper;
import com.oito.auth.mapper.UserTypeRequestMapper;
import com.oito.auth.otp.OTPHandler;
import com.oito.auth.service.NotificationService;
import com.oito.auth.service.PrivilegeService;
import com.oito.auth.service.RoleService;
import com.oito.auth.service.UserMetadataService;
import com.oito.auth.service.UserPrivilegeAssignmentService;
import com.oito.auth.service.UserService;
import com.oito.auth.util.MaskUtils;
import com.oito.auth.util.UserUtils;
import com.oito.auth.v1.mapper.OTPLoginMapper;
import com.oito.auth.validator.PasswordHandler;
import com.oito.auth.validator.UserValidator;
import com.oito.common.auth.jwt.JWETokenHandler;
import com.oito.common.auth.jwt.JWSTokenHandler;
import com.oito.common.auth.jwt.TokenType;
import com.oito.common.exception.ApiException;
import com.oito.common.usercontext.UserContext;
import com.oito.common.usercontext.UserContextStore;
import com.oito.common.util.Booleans;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Auth0 specific implementation of UserService
 *
 * @author Dileep
 *
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Value("${oito.reset-password.expiry-in-days:7}")
	private int resetPasswordExpiryInDays;

	private static final String USER_ID = "user_id";

	@Value("${oito.phone.country-codes}")
	private String supportedCountryCodes;

	@Value("${oito.phone.default-country-code}")
	private String defaultCountryCode;

	@Value("${oito.username.enable-username:#{false}}")
	private boolean enableUserName;

	@Value("${oito.auth.user-otp-access:false}")
	private boolean accessUserOtp;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordHandler passwordHandler;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UserTypeDAO userTypeDAO;

	@Autowired
	private UserLoginHistoryRepository userLoginHistoryRepository;

	@Autowired
	private JWSTokenHandler jwsTokenHandler;

	@Autowired
	private JWETokenHandler jweTokenHandler;

	@Autowired
	private OTPHandler otpHandler;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserMetadataService userMetadataService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private UserPrivilegeAssignmentService privilegeAssignmentService;

	@Autowired
	private TokenHandler tokenHandler;

	@Autowired
	private PrivilegeDAO privilegeDAO;

	@Autowired
	private UserContextStore userContextStore;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserContextService userContextService;

	@Value("${oito.phone.mandatory:#{true}}")
	private boolean isPhoneMandatory;

	@Autowired
	private AppUserToUserMapper appUserToUserMapper;

	@Autowired
	private UserSignUpRequestToUserMapper userSignUpRequestToUserMapper;

	@Autowired
	private ClientToUserMapper clientToUserMapper;

	@Autowired
	private UserLoginHistoryMapper userLoginHistoryMapper;

	@Autowired
	private UserTypeRequestMapper userTypeRequestMapper;

	@Autowired
	private AppUserTOSignUpResponseMapper mapper;

	@Autowired
	private ObjectValidator validator;

	@Autowired
	private OTPLoginMapper otpLoginMapper;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.auth.service.UserService#signUpUser(com.auth.common.to.AppUserTO)
	 */

	@Override
	@Transactional
	public AppUserTO signUpUser(final UserSignUpRequest request) {
		populatePassword(request);

		populatePhoneNumber(request);
		populateCommunicationChannel(request);
		validateSignUp(request);
		final var isEmailChanged = replaceFirstNonASCIICharacterInEmail(request);
		if (!enableUserName) {
			populateUserName(request);
			final var userFromDb = getDuplicateUser(request.getUseremail(), request.getPhoneNo(),
					request.getUserType());
			if (userFromDb != null) {
				userValidator.populateEmailWarnings(userFromDb, isEmailChanged);
				return userFromDb;
			}
		} else {
			userValidator.validateCustomSignUpRequest(request);
			validateDuplicateUser(request.getUserName(), request.getUseremail(), request.getPhoneNo());
		}
		final var signUpResponse = persistUserToDatabase(request);
		userMetadataService.save(request.getCustomFields(), signUpResponse.getUserId());
		persistRolePrivileges(request.getRoleList(), request.getPrivilegeList(), signUpResponse);
		userValidator.populateEmailWarnings(signUpResponse, isEmailChanged);
		signUpResponse.setSignupToken(buildVerificationToken(signUpResponse.getUserId()));
		return signUpResponse;
	}

	private void validateSignUp(final UserSignUpRequest request) {
		userValidator.validateSignUpRequest(request);
		validateOtp(request);
		userMetadataService.validateAdminCreation(request.getCustomFields());
	}

	private void validateOtp(final UserSignUpRequest request) {
		if (!isValidOtp(request.getPhoneNo(), request.getOtp(), request.getOtpId())) {
			throw new AuthException(AuthErrorCode.OTP_NOT_FOUND_OR_EXPIRED);
		}
	}

	private boolean isValidOtp(final String phoneNo, final String otp, final Long otpId) {
		return ((!nonNull(otp) || !nonNull(otpId)) || otpHandler.verifyOTP(otpId.longValue(), otp, phoneNo));
	}

	@Override
	@Transactional
	public SignUpResponse signup(final UserSignUpRequest request) {
		try {
			log.info("Sign up request {}", request);
			final var userTo = signUpUser(request);
			log.info("Sign up Response : {}", userTo);
			final var response = mapper.toVO(userTo);
			decorateSignUpResponse(userTo, response);
			return response;
		} catch (final AuthException exception) {
			log.error("Error while handling sign up request", exception);
			return SignUpResponse.failure(exception.getAuthErrorCode(), exception);
		} catch (final Exception e) {
			log.error("Error while handling sign up request", e);
			return SignUpResponse.failure(e.getMessage());
		}
	}

	private void decorateSignUpResponse(final AppUserTO userTo, final SignUpResponse response) {
		response.setStatus(Status.SUCCESS);
		response.setVerificationToken(userTo.getSignupToken());
		if (response.getErrorCode() == null) {
			return;
		}
		final var action = response.getErrorCode().getErrorAction();
		response.setErrorAction(action);
		if (action.equals(ErrorAction.GET_EMAIL_PASSWORD)) {
			response.setMaskedUserEmail(MaskUtils.maskEmailAddress(userTo.getUseremail(), '*'));
		}
	}

	private void populateCommunicationChannel(final UserSignUpRequest request) {
		if (StringUtils.isBlank(request.getUseremail())) {
			removeCommunicationChannel(request, CommunicationChannel.EMAIL);
		}
	}

	private boolean replaceFirstNonASCIICharacterInEmail(final UserSignUpRequest request) {
		final var email = request.getUseremail();
		if (StringUtils.isBlank(email)) {
			return false;
		}
		request.setUseremail(userValidator.replaceFirstNonASCIICharacter(request.getUseremail()));
		return !email.equals(request.getUseremail());
	}

	private void persistRolePrivileges(final List<String> roles, final List<PrivilegeRequest> privileges,
			final AppUserTO user) {
		final var roleList = roleService.getRoleList(roles);
		final var privilegeList = privilegeService.getPrivilegeList(privileges);
		privilegeAssignmentService.save(user, roleList, privilegeList);
	}

	@AllArgsConstructor
	private enum PortalUserError {
		EXISTS_WITH_PHONE_EMAIL(AuthErrorCode.USER_EXISTS_WITH_PHONE_EMAIL,
				AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_PHONE_EMAIL),
		EXISTS_WITH_PHONE(AuthErrorCode.USER_EXISTS_WITH_PHONE, AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_PHONE),
		EXISTS_WITH_EMAIL(AuthErrorCode.USER_EXISTS_WITH_EMAIL, AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_EMAIL),
		SCATTERED(AuthErrorCode.SCATTERED_USER_INFO, AuthErrorCode.SCATTERED_USER_INFO);

		private final AuthErrorCode samePortalErrorCode;
		private final AuthErrorCode differentPortalErrorCode;

		public AuthErrorCode getAuthErrorCode(final boolean samePortal) {
			return samePortal ? samePortalErrorCode : differentPortalErrorCode;
		}
	}

	@Override
	public void populatePhoneNumber(final UserLoginRequest request) {
		request.setPhoneNo(getCompletePhoneNumber(request.getPhoneNo()));
	}

	private void populatePhoneNumber(final UserSignUpRequest request) {
		if (StringUtils.isEmpty(request.getPhoneNo()) && !isPhoneMandatory) {
			request.setPhoneNo(StringUtils.EMPTY);
			removeCommunicationChannel(request, CommunicationChannel.SMS);
		} else {
			request.setPhoneNo(getCompletePhoneNumber(request.getPhoneNo()));
		}
	}

	private void removeCommunicationChannel(final UserSignUpRequest request,
			final CommunicationChannel channelToRemove) {
		final Set<CommunicationChannel> communicationChannel = new HashSet<>(request.getCommunicationChannels());
		communicationChannel.removeIf(channel -> channel.equals(channelToRemove));
		request.setCommunicationChannels(communicationChannel);
	}

	private void populatePhoneNumber(final AppUserTO user) {
		user.setPhoneNo(getCompletePhoneNumber(user.getPhoneNo()));
	}

	private void populateUserName(final UserSignUpRequest request) {
		if (StringUtils.isBlank(request.getUserName())) {
			request.setUserName(
					StringUtils.isNotBlank(request.getUseremail()) ? request.getUseremail() : request.getPhoneNo());
		}
	}

	private String getCompletePhoneNumber(final String phone) {
		if (StringUtils.isBlank(phone) || StringUtils.startsWith(phone, "+")) {
			return phone;
		}
		return defaultCountryCode + phone;
	}

	private AppUserTO getDuplicateUser(final String email, final String phone, final AuthUserType type) {
		final var userByEmail = getUserByEmail(email);
		final var userByPhone = getUserByPhone(phone);
		AuthErrorCode errorCode;
		if (userByEmail.isEmpty() && userByPhone.isEmpty()) {
			return null;
		}
		final var isSamePortal = isSamePortalUsers(userByEmail, userByPhone, type);
		if (userByEmail.isPresent() && userByPhone.isPresent()) {
			if (userByEmail.get().getUserId().equals(userByPhone.get().getUserId())) {
				errorCode = PortalUserError.EXISTS_WITH_PHONE_EMAIL.getAuthErrorCode(isSamePortal);
			} else {
				errorCode = PortalUserError.SCATTERED.getAuthErrorCode(isSamePortal);
			}
		} else if (userByEmail.isPresent()) {
			errorCode = PortalUserError.EXISTS_WITH_EMAIL.getAuthErrorCode(isSamePortal);
		} else {
			errorCode = PortalUserError.EXISTS_WITH_PHONE.getAuthErrorCode(isSamePortal);
		}
		final var duplicateUser = userByEmail.orElseGet(userByPhone::get);
		if (errorCode != null) {
			setError(duplicateUser, errorCode);
		}
		return duplicateUser;
	}

	@Override
	public void validateDuplicateUser(final String userName, final String email, final String phone) {
		final var userList = userDAO.getUsersByUserNameOrEmailOrPhone(userName, email, phone);
		if (!userList.isEmpty()) {
			final List<String> errorMessages = new ArrayList<>();
			userList.forEach(user -> {
				log.info("AppUserTO in loop {}", user);
				if (Objects.equals(user.getUserName(), userName)) {
					errorMessages.add(AuthErrorCode.USER_EXISTS_WITH_USERNAME.getErrorCode());
				}
				if (Objects.equals(user.getUseremail(), email)) {
					errorMessages.add(AuthErrorCode.USER_EXISTS_WITH_EMAIL.getErrorCode());
				}
				if (Objects.equals(user.getPhoneNo(), phone)) {
					errorMessages.add(AuthErrorCode.USER_EXISTS_WITH_PHONE.getErrorCode());
				}
			});
			log.info("Error Messages {}", errorMessages);
			throw new AuthException(AuthErrorCode.TOO_MANY_USERS_FOUND, errorMessages);
		}

	}

	private boolean isSamePortalUsers(final Optional<AppUserTO> user1, final Optional<AppUserTO> user2,
			final AuthUserType type) {
		return isUserTypeExists(user1, type) && isUserTypeExists(user2, type);
	}

	private boolean isUserTypeExists(final Optional<AppUserTO> user, final AuthUserType type) {
		return user.isEmpty() || isUserTypeExists(user.get(), type);
	}

	@Override
	public boolean isUserTypeExists(@NonNull final AppUserTO user, final AuthUserType type) {
		return user.getUserTypes().stream().anyMatch(userType -> userType.getType() == type);
	}

	@Override
	public AppUserTO generateVerificationToken(final String email) {
		final var user = getUserByEmail(email)
				.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_APP_USER_DATA));
		user.setSignupToken(buildVerificationToken(user.getUserId()));
		return user;
	}

	@Override
	public AppUserTO generateVerificationToken(final ChangePasswordRequest changePasswordRequest) {
		final var user = appUserToUserMapper.toVO(getDbUser(changePasswordRequest.getUseremail(),
				changePasswordRequest.getPhoneNo(), changePasswordRequest.getUserName()));
		user.setSignupToken(buildVerificationToken(user.getUserId()));
		return user;
	}

	private void populatePassword(final UserSignUpRequest signUpRequest) {
		if (signUpRequest.isGeneratePassword()) {
			signUpRequest.setPassword(passwordHandler.generatePassword());
		}
	}

	private String buildVerificationToken(final Long userId) {
		return generateJsonWebToken(Map.of(USER_ID, userId.toString()), resetPasswordExpiryInDays);
	}

	/**
	 * Persist the IDP user to application Database, using the actual user
	 * attributes provided by the end user along with the IDP specific attributes
	 * which got created
	 *
	 */
	private AppUserTO persistUserToDatabase(final UserSignUpRequest request) {
		/*
		 * Persist the user created to application Database using the actual user
		 * attributes along with the Auth0 specific attributes created
		 */
		// user here is a prepopulated User instance

		final var user = userSignUpRequestToUserMapper.toEntity(request);
		if (StringUtils.isNotBlank(request.getPassword())) {
			user.setUserSecretHash(encodePassword(request.getPassword()));
		}
		try {
			saveUser(user);
			final var userTO = appUserToUserMapper.toVO(user);
			addUserType(userTO, request.getUserType());
			return userTO;
		} catch (final Exception exception) {
			log.error("Failed to save to db", exception);
			throw new AuthException(AuthErrorCode.UNKNOWN_EXCEPTION, exception);
		}
	}

	@Override
	public AppUserTO saveClientUser(final ClientTO request) {
		try {
			final var user = clientToUserMapper.toEntity(request);
			user.setClientSecretHash(encodePassword(request.getSecretKey()));
			user.setSocialFlag(Boolean.FALSE);
			final var userFromDb = saveUser(user);
			final var userTO = appUserToUserMapper.toVO(userFromDb);
			addUserType(userTO, request.getClientType());
			return userTO;
		} catch (final Exception exception) {
			log.error("Failed to save to db", exception);
			throw new AuthException(AuthErrorCode.UNKNOWN_EXCEPTION, exception);
		}
	}

	private UserTypeTO addUserType(final Long userId, final AuthUserType type) {
		return userTypeDAO.save(new UserTypeTO(userId, type, true));
	}

	@Override
	@Transactional
	public AppUserTO resetPassword(final ChangePasswordRequest changePasswordRequest) {
		userValidator.validateResetPassword(changePasswordRequest);
		final var user = validateToken(changePasswordRequest.getToken());
		user.setResetToken(null);
		return resetUserSiginDetailsInDb(changePasswordRequest, user);
	}

	@Override
	@Transactional
	public AppUserTO changePassword(final ChangePasswordRequest changePasswordRequest) {
		userValidator.validateChangePassword(changePasswordRequest);
		final var user = findById(userContextService.getUserId())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		ApiException.ifThrow(changePasswordRequest.getCurrentPassword() == null && user.getUserSecretHash() != null,
				AuthErrorCode.EMPTY_PASSWORD);
		tryLogin(changePasswordRequest, user);
		return resetUserSiginDetailsInDb(changePasswordRequest, user);
	}

	private AppUserTO resetUserSiginDetailsInDb(final ChangePasswordRequest changePasswordRequest, final User user) {
		resetStaySignedInDetails(user);
		user.setUserSecretHash(encodePassword(changePasswordRequest.getNewPassword()));
		saveUser(user);
		return appUserToUserMapper.toVO(user);
	}

	private void resetStaySignedInDetails(final User user) {
		user.setLastAccessToken1("");
		user.setLastAccessToken2("");
		user.setStaySignedIn(Boolean.FALSE);
		user.setTokenExpiryTimestamp(null);
	}

	@Override
	public AppUserTO generateResetPasswordToken(final String userEmail, final String phoneNo, final String userName) {
		final var currentUser = getDbUser(userEmail, phoneNo, userName);
		final var resetToken = generateJsonWebToken(Map.of(USER_ID, currentUser.getUserId().toString()),
				resetPasswordExpiryInDays);
		currentUser.setResetToken(resetToken);
		saveUser(currentUser);
		final var appUserTO = appUserToUserMapper.toVO(currentUser);
		appUserTO.setMaskedUserEmail(MaskUtils.maskEmailAddress(appUserTO.getUseremail(), '*'));
		return appUserTO;
	}

	@Override
	public Optional<UserTypeTO> filterUserTypeTO(final Set<UserTypeTO> userTypes, final AuthUserType userType) {
		return userTypes.stream().filter(userTypeFromList -> userTypeFromList.getType().equals(userType)).findFirst();
	}

	@Override
	public boolean validateResetPasswordToken(final String token) {
		return validateToken(token) != null;
	}

	private User validateToken(final String token) {
		final Map<String, String> map = jwsTokenHandler.decodeToken(token);
		return userRepository.findById(Long.valueOf(map.get(USER_ID)))
				.filter(appUser -> token.equals(appUser.getResetToken()))
				.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));
	}

	private void tryLogin(final ChangePasswordRequest changePasswordRequest, final User user) {
		ApiException.ifThrow(
				(changePasswordRequest.getCurrentPassword() != null
						&& !matchPassword(changePasswordRequest.getCurrentPassword(), user.getUserSecretHash())),
				AuthErrorCode.INVALID_PASSWORD);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.auth.service.UserService#getUserByEmail(java.lang.String)
	 */
	@Override
	public Optional<AppUserTO> getUserByEmail(final String userEmail) {
		if (StringUtils.isBlank(userEmail)) {
			return Optional.empty();
		}
		return populateAdditionalUserDetails(userDAO.getUsersByUserEmail(userEmail));
	}

	private Optional<AppUserTO> findByUserEmailAndType(final String userEmail, @NonNull final AuthUserType type) {
		if (StringUtils.isBlank(userEmail)) {
			return Optional.empty();
		}
		return populateAdditionalUserDetails(userDAO.findByUserEmailAndType(userEmail, type));
	}

	private Optional<AppUserTO> populateAdditionalUserDetails(final Optional<AppUserTO> user) {
		user.ifPresent(this::setAdditionalUserDetails);
		return user;
	}

	private void setAdditionalUserDetails(final AppUserTO usr) {
		usr.setCustomFields(userMetadataService.findUserMetadataMap(usr.getUserId()));
		usr.setPrivilegeList(privilegeDAO.getUserPrivileges(usr.getUserId()));
	}

	@Override
	public Optional<AppUserTO> getUserByUserName(final String userName) {
		if (StringUtils.isBlank(userName)) {
			return Optional.empty();
		}
		return userDAO.getUsersByUserName(userName);
	}

	private User getDbUser(final String email, final String phoneNo, final String userName) {
		ApiException.ifThrow(StringUtils.isAllBlank(email, phoneNo, userName),
				AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB);
		return findByUseremail(email).or(() -> findByPhoneNo(phoneNo)).or(() -> findByUserName(userName))
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
	}

	private Optional<User> findById(final Long userId) {
		if (userId == null) {
			return Optional.empty();
		}
		return userRepository.findById(userId);
	}

	private Optional<User> findByUseremail(final String useremail) {
		if (StringUtils.isBlank(useremail)) {
			return Optional.empty();
		}
		return userRepository.findByUseremail(useremail);
	}

	private Optional<User> findByPhoneNo(final String phoneNo) {
		if (StringUtils.isBlank(phoneNo)) {
			return Optional.empty();
		}
		if (userDAO.countByPhoneNo(phoneNo) > 1) {
			throw new AuthException(AuthErrorCode.PHONE_MULTIPLE_ACCOUNTS);
		}
		return userRepository.findByPhoneNo(phoneNo);
	}

	private Optional<User> findByUserName(final String userName) {
		if (StringUtils.isBlank(userName)) {
			return Optional.empty();
		}
		return userRepository.findByUserName(userName);
	}

	private Optional<User> findByUserEmailAndUserType(final String email, final AuthUserType userType) {
		if (StringUtils.isBlank(email)) {
			return Optional.empty();
		}
		return userRepository.findByUseremailAndUserType(email, userType);
	}

	@Override
	public Optional<User> findByPhoneNoAndUserType(final String phoneNo, final AuthUserType userType) {
		if (StringUtils.isBlank(phoneNo)) {
			return Optional.empty();
		}
		final var users = userRepository.findByPhoneNoAndUserType(phoneNo, userType);
		if (users.size() > 1) {
			throw new AuthException(AuthErrorCode.TOO_MANY_USERS_FOUND);
		}
		if (users.size() == 1) {
			return Optional.of(users.get(0));
		}
		return Optional.empty();
	}

	@Override
	public AppUserTO verify(final String token) {
		try {
			final Map<String, String> userInfo = jwsTokenHandler.decodeToken(token);
			final var user = findById(Long.valueOf(userInfo.get(USER_ID)))
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			user.setVerified(Boolean.TRUE);
			saveUser(user);
			return appUserToUserMapper.toVO(user);
		} catch (final AuthException e) {
			throw e;
		} catch (final Exception e) {
			log.error(e.getMessage());
			throw new AuthException(AuthErrorCode.UNKNOWN_EXCEPTION, e);
		}
	}

	@Override
	public AppUserTO update(final AppUserTO appUser) {
		try {
			log.debug("User Update requested {}", appUser);
			populatePhoneNumber(appUser);
			userValidator.validateUpdate(appUser);
			validatePhone(appUser);
			final var user = findById(appUser.getUserId()).or(() -> findByUseremail(appUser.getUseremail()))
					.or(() -> findByUserName(appUser.getUserName()))
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			userContextService.verifyUserEditAccess(user.getUserId());
			updateEmailIfBlank(appUser, user);
			populateCommunicationChannel(appUser, user);
			final var customFieldMap = updateCustomFields(appUser, user);
			final var updatedUser = updateUser(appUser, user);
			updatedUser.setCustomFields(customFieldMap);
			return updatedUser;
		} catch (final AuthException ex) {
			log.error("failed to update user", ex);
			throw ex;
		} catch (final Exception e) {
			log.error("failed to update user", e);
			throw new AuthException(AuthErrorCode.UNKNOWN_EXCEPTION, e);
		}
	}

	private void validatePhone(final AppUserTO appUserTO) {
		if (StringUtils.isNotBlank(appUserTO.getPhoneNo())) {
			userValidator.validatePhone(appUserTO.getPhoneNo(), appUserTO.getPhoneCountryCode());
			final var userByPhone = getUserByPhone(appUserTO.getPhoneNo());
			userByPhone.filter(usr -> isNotSameUser(appUserTO, usr)).ifPresent(usr -> {
				throw new AuthException(AuthErrorCode.USER_EXISTS_WITH_PHONE);
			});
		}
	}

	private boolean isNotSameUser(final AppUserTO appUser, final AppUserTO user) {
		return (appUser.getUserId() != null && appUser.getUserId().longValue() != user.getUserId().longValue())
				|| (appUser.getUseremail() != null && !appUser.getUseremail().equals(user.getUseremail()))
				|| (appUser.getUserName() != null && !appUser.getUserName().equals(user.getUserName()));
	}

	private void updateEmailIfBlank(final AppUserTO appUser, final User user) {
		if (StringUtils.isNotBlank(appUser.getUseremail()) && StringUtils.isBlank(user.getUseremail())) {
			ApiException.ifThrow(userDAO.countByUseremail(appUser.getUseremail()) > 0,
					AuthErrorCode.USER_EXISTS_WITH_EMAIL);
			user.setUseremail(appUser.getUseremail());
		}
	}

	private void populateCommunicationChannel(final AppUserTO appUser, final User user) {
		if (CollectionUtils.isEmpty(appUser.getCommunicationChannels())) {
			final var communicationChannels = new HashSet<>(user.getCommunicationChannels());
			if (StringUtils.isNotEmpty(appUser.getPhoneNo())) {
				communicationChannels.add(CommunicationChannel.SMS);
			}
			// If email communication channel is missing in db, trying to set email
			// communication channel as well
			if (StringUtils.isNotEmpty(user.getUseremail())) {
				communicationChannels.add(CommunicationChannel.EMAIL);
			}
			appUser.setCommunicationChannels(communicationChannels);
		}
	}

	private Map<String, String> updateCustomFields(final AppUserTO appUser, final User user) {
		var customFieldMap = Map.<String, String>of();
		if (MapUtils.isNotEmpty(appUser.getCustomFields())) {
			customFieldMap = new HashMap<>(appUser.getCustomFields());
		}
		userMetadataService.merge(appUser.getCustomFields(), user.getUserId());
		return customFieldMap;
	}

	private AppUserTO updateUser(final AppUserTO appUser, final User user) {
		appUser.setUseremail(user.getUseremail());
		appUser.setUserName(user.getUserName());
		return updateInternal(appUser, user);
	}

	private AppUserTO updateInternal(final AppUserTO appUser, final User user) {
		appUserToUserMapper.mapEntity(appUser, user);
		copyExtraFields(appUser, user);
		return appUserToUserMapper.toVO(saveUser(user));
	}

	@Override
	public AppUserTO updateUser(final AppUserTO appUser) {
		final var user = findById(appUser.getUserId())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		return updateInternal(appUser, user);
	}

	/**
	 * Manually copy the columns not handled by model mapper during update
	 *
	 * @param appUser
	 * @param user
	 */
	private void copyExtraFields(final AppUserTO appUser, final User user) {
		if (CollectionUtils.isNotEmpty(appUser.getCommunicationChannels())
				&& !appUser.getCommunicationChannels().equals(user.getCommunicationChannels())) {
			user.getCommunicationChannels().clear();
			user.getCommunicationChannels().addAll(appUser.getCommunicationChannels());
		}
	}

	@Override
	public AppUserTO validateAndGenerateVerificationToken(final ChangePasswordRequest changePasswordRequest,
			final String token) {
		User user;
		if (token != null) {
			final Map<String, String> claimMap = jwsTokenHandler.decodeToken(token);
			final var userId = Long.valueOf(claimMap.get(USER_ID));
			user = findById(userId).orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));
		} else {
			user = getDbUser(changePasswordRequest.getUseremail(), changePasswordRequest.getPhoneNo(),
					changePasswordRequest.getUserName());
		}
		final var userTO = appUserToUserMapper.toVO(user);
		userTO.setSignupToken(buildVerificationToken(user.getUserId()));
		return userTO;
	}

	@Override
	@Transactional
	public void saveLoginHistory(final Long userId, final String sessionId, final AuthProvider authProvider) {
		final var userLoginHistoryBean = new UserLoginHistoryBean();
		userLoginHistoryBean.setUserId(userId);
		userLoginHistoryBean.setSessionId(sessionId);
		userLoginHistoryBean.setLoginTimestamp(LocalDateTime.now());
		userLoginHistoryBean.setAuthProvider(authProvider);
		userLoginHistoryRepository.save(userLoginHistoryMapper.toEntity(userLoginHistoryBean));
	}

	@Override
	public SimpleResponse logOut(final String sessionId, final Long userId, final String accessToken) {
		try {
			log.info("Logout triggered for user : {} and session : {}", userId, sessionId);

			userLoginHistoryRepository.updateByUserIdAndSessionId(userId, sessionId);
			final var user = findById(userId)
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			final var tokenType = jweTokenHandler.decodeToken(accessToken).get(Constants.TOKEN_TYPE_KEY);

			saveStaySignedInDetails(user, TokenType.valueOf(tokenType));

			return SimpleResponse.success();
		} catch (final AuthException e) {
			log.warn("Session Id mismatch while login.. SessionId: {} , UserId : {}", sessionId, userId);
			log.error("AuthException saving login history", e);
			return SimpleResponse.failure(e.getAuthErrorCode());
		} catch (final Exception e) {
			log.error("Unknow error while saving login history", e);
			return SimpleResponse.failure(AuthErrorCode.UNABLE_TO_SAVE_LOGIN_HISTORY);
		}
	}

	private void saveStaySignedInDetails(final User user, final TokenType tokenType) {
		log.info("Inside logout for staySignedIn");
		if (tokenType == TokenType.STAY_SIGNED_IN) {
			log.info("Resetting staySignedIn");
			resetStaySignedInDetails(user);
			saveUser(user);
		}
	}

	private User saveUser(final User user) {
		return userRepository.save(user);
	}

	@Override
	public List<AppUserTO> getUsersByEmails(final List<String> emailList) {
		return userDAO.getUsersByEmails(emailList);
	}

	@Override
	public List<AppUserTO> getUsersByPhoneNos(final List<String> phoneNos) {
		return userDAO.getUsersByPhoneNos(phoneNos);
	}

	@Override
	public List<String> getSupportedCountryCodes() {
		return Arrays.asList(StringUtils.split(supportedCountryCodes, ","));
	}

	@Override
	public Optional<AppUserTO> getUserByPhone(final String phone) {
		try {
			if (StringUtils.isBlank(phone)) {
				return Optional.empty();
			}
			return userDAO.getUserByPhone(phone);
		} catch (final Exception e) {
			log.error("Failed to get user by phone " + phone, e);
			throw new AuthException(AuthErrorCode.TOO_MANY_USERS_FOUND);
		}
	}

	@Override
	public OTPResponse generateOTP(final OTPRequest request) {
		final var user = request.getUserId() == null ? AppUserTO.empty()
				: userDAO.findById(request.getUserId())
						.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		final var phoneNumber = StringUtils.isNotBlank(request.getPhone()) ? request.getPhone() : user.getPhoneNo();
		// Send OTP as email only if email is present in the request
		final var otpId = otpHandler.generate(user.getUserId(), phoneNumber, request.getEmail(), request);
		final var response = new OTPResponse(Status.SUCCESS, otpId);
		if (phoneNumber != null) {
			response.setMaskedPhone(MaskUtils.maskPhone(phoneNumber));
		}
		return response;
	}

	@Override
	public void resendOTP(final OTPRequest request) {
		final var user = request.getUserId() == null ? AppUserTO.empty()
				: userDAO.findById(request.getUserId())
						.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		final var phoneNumber = StringUtils.isNotBlank(request.getPhone()) ? request.getPhone() : user.getPhoneNo();
		// Send OTP as email only if email is present in the request
		final var email = request.getEmail();
		otpHandler.resend(phoneNumber, email, request);
	}

	@Override
	public boolean verifyOTP(final long otpId, final String otp) {
		return otpHandler.verifyOTP(otpId, otp);
	}

	@Override
	public Map<Long, AppUserTO> getUsersByUserIds(final List<Long> userIds) {
		final var appUserMap = userDAO.getUsersByUserIds(userIds);

		appUserMap.entrySet().forEach(map -> {
			final var appUserTo = map.getValue();
			appUserTo.setCustomFields(userMetadataService.findUserMetadataMap(appUserTo.getUserId()));

		});

		return appUserMap;
	}

	@Override
	public Optional<AppUserTO> getUserById(final Long id) {
		return populateAdditionalUserDetails(userDAO.findById(id));
	}

	@Transactional
	@Override
	public AppUserTO addUserTypeSecure(final UserTypeRequest request) {
		try {
			final var user = findByUseremail(request.getUseremail()).or(() -> findByPhoneNo(request.getPhoneNo()))
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			validateCredentials(request, user);
			final var userTO = appUserToUserMapper.toVO(user);
			addUserType(userTO, request.getUserType());
			userMetadataService.merge(request.getCustomFields(), user.getUserId());
			persistRolePrivileges(request.getRoleList(), request.getPrivilegeList(), userTO);
			return userTO;
		} catch (final AuthException ex) {
			log.error("AuthException Failed to add user type Request ", ex);
			throw ex;
		} catch (final Exception e) {
			log.error("Exception Failed to add user type. Request ", e);
			throw new AuthException(AuthErrorCode.ADD_USER_TYPE_FAILURE);
		}
	}

	private void validateCredentials(final UserTypeRequest request, final User user) {
		if (!Strings.isNullOrEmpty(request.getPassword())) {
			attemptLogin(request.getPassword(), user.getUserSecretHash());
		} else if ((request.getPhoneNo() != null && !user.getPhoneNo().equals(request.getPhoneNo()))
				|| !isValidOtp(user.getPhoneNo(), request.getOtp(), request.getOtpId())) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	private AppUserTO addUserType(final String userEmail, final AuthUserType userType) {
		try {
			final var user = findByUseremail(userEmail)
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			final var userTO = appUserToUserMapper.toVO(user);
			addUserType(userTO, userType);
			return userTO;
		} catch (final AuthException ex) {
			log.error("Failed to add user type. Request ", ex);
			throw ex;
		} catch (final Exception e) {
			log.error("Failed to add user type. Request ", e);
			throw new AuthException(AuthErrorCode.ADD_USER_TYPE_FAILURE);
		}
	}

	@Override
	public void addUserType(final AppUserTO userTO, final AuthUserType userType) {
		try {
			if (userTO.getUserTypes() == null) {
				userTO.setUserTypes(new HashSet<>());
			}
			userTO.getUserTypes().add(addUserType(userTO.getUserId(), userType));
		} catch (final AuthException ex) {
			log.error("Failed to add user type. Request :" + userTO, ex);
			throw ex;
		} catch (final Exception e) {
			log.error("Failed to add user type. Request :" + userTO, e);
			throw new AuthException(AuthErrorCode.ADD_USER_TYPE_FAILURE);
		}
	}

	@Override
	public AppUserTO getUserForLogin(final UserLoginRequest request) {
		return getUserByEmaiOrPhoneOrUserName(request.getUseremail(), request.getPhoneNo(), request.getUserName());
	}

	private AppUserTO getUserByEmaiOrPhoneOrUserName(final String userEmail, final String phone,
			final String userName) {
		return getUserByEmail(userEmail).or(() -> getUserByUserName(userName)).or(() -> getUserByPhone(phone))
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
	}

	private void attemptLogin(final String password, final String secretHash) {
		if (!matchPassword(password, secretHash)) {
			throw new AuthException(AuthErrorCode.IDP_API_EXCEPTION);
		}
	}

	@Override
	public AppUserTO verifyAuth(final UserLoginRequest request) {
		try {
			final var user = findByUseremail(request.getUseremail())
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
			attemptLogin(request.getPassword(), user.getUserSecretHash());
			return appUserToUserMapper.toVO(user);
		} catch (final AuthException ex) {
			log.error("Failed to verify user. Request :" + request, ex);
			throw ex;
		} catch (final Exception e) {
			log.error("Failed to verify user. Request :" + request, e);
			throw new AuthException(AuthErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	@Override
	public Optional<AppUserTO> getCurrentLoggedinUserData(final String accessToken) {
		final var token = tokenHandler.getAccessTokenFromAuthorization(accessToken);
		final var userContext = userContextStore.getUserContext();
		if (null != userContext) {
			final var userData = extractUserFromUserContext(userContext);
			userData.ifPresent(user -> setAccessToken(user, token));
			log.info("Current userData UserContext not null {}", userData);
			return userData;
		}
		final var tokenClaims = jweTokenHandler.decodeToken(token);
		final var userIdStr = tokenClaims.get("userId");
		if (null != userIdStr) {
			final var user = getUserById(Long.valueOf(userIdStr));
			user.ifPresent(usr -> setAccessToken(usr, token));
			log.info("Current userData UserContext null {}", user);
			return user;
		}
		return Optional.empty();
	}

	private Optional<AppUserTO> extractUserFromUserContext(final UserContext userContext) {
		final var user = getUserById(userContext.getUserId());
		user.ifPresent(usr -> setAdditonalUserDetailsFromUserContext(userContext, usr));
		return user;
	}

	private void setAdditonalUserDetailsFromUserContext(final UserContext userContext, final AppUserTO usr) {
		usr.setCustomFields(userContext.getCustomFields());
		usr.setPrivilegeList(userContext.getPrivilegeList());
	}

	private void setAccessToken(final AppUserTO usr, final String token) {
		if (usr.getStaySignedIn().booleanValue()) {
			usr.setAccessToken(usr.getLastAccessToken1());
		} else {
			usr.setAccessToken(token);
		}
	}

	@Override
	public Optional<AppUserTO> getImersonatedUserData() {
		final var userContext = userContextStore.getUserContext();
		if (null != userContext && null != userContext.getImpersonatedUserContext()) {
			return extractUserFromUserContext(userContext.getImpersonatedUserContext());
		}
		return Optional.empty();
	}

	private void setError(final AppUserTO user, final AuthErrorCode error) {
		user.setError(error.getErrorMessage());
		user.setErrorCode(error.name());
		user.setErrorAction(error.getErrorAction());
	}

	@Override
	public String generateTokenAndVerifyOTP(final OTPPhoneVerificationRequest request) {
		final var user = verifyOTPAndValidatePhone(otpLoginMapper.map(request));
		if (user != null) {
			final var resetToken = buildVerificationToken(user.getUserId());
			user.setResetToken(resetToken);
			saveUser(user);
			return resetToken;
		}
		return null;
	}

	@Override
	public User verifyOTPAndValidatePhone(final UserLoginRequest request) {
		validator.validate(request);
		final var otpStatus = otpHandler.verifyOTP(request.getOtpId().longValue(), request.getOtp(),
				request.getPhoneNo());
		if (otpStatus) {
			return findByPhoneNoAndUserType(request.getPhoneNo(), request.getUserType())
					.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		}
		return null;
	}

	@Override
	@Transactional
	public AppUserTO changeEmail(final ChangeEmailRequest changeEmailRequest) {
		userValidator.validateEmail(changeEmailRequest.getNewUseremail());
		userContextService.verifyUserEditAccess(changeEmailRequest.getUserId());
		final var currentUser = findById(changeEmailRequest.getUserId());
		final var requestEmail = changeEmailRequest.getNewUseremail();
		changeEmailRequest
				.setNewUseremail(userValidator.replaceFirstNonASCIICharacter(changeEmailRequest.getNewUseremail()));

		validateChangeEmail(changeEmailRequest, currentUser);
		final var user = currentUser.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		user.setUseremail(changeEmailRequest.getNewUseremail());
		if (!enableUserName) {
			user.setUserName(user.getUseremail());
		}
		if (CollectionUtils.isNotEmpty(changeEmailRequest.getCommunicationChannels())) {
			user.getCommunicationChannels().addAll(changeEmailRequest.getCommunicationChannels());
		}
		nullifySocialDetails(user);
		userDAO.save(user);
		final var modifiedUser = appUserToUserMapper.toVO(user);
		if (!requestEmail.equals(modifiedUser.getUseremail())) {
			modifiedUser.setWarning(AuthErrorCode.WARNING_INVALID_CHARS_EMAIL.getMessage());
			modifiedUser.setWarningCode(AuthErrorCode.WARNING_INVALID_CHARS_EMAIL.getCode());
		}
		return modifiedUser;
	}

	private void nullifySocialDetails(final User user) {
		user.setSocialFlag(Boolean.FALSE);
		user.setFacebookId(null);
		user.setGoogleId(null);
	}

	private void nullifySocialDetails(final AppUserTO user) {
		user.setSocialFlag(Boolean.FALSE);
		user.setFacebookId(StringUtils.EMPTY);
		user.setGoogleId(StringUtils.EMPTY);
		user.setLineId(StringUtils.EMPTY);
	}

	private void validateChangeEmail(final ChangeEmailRequest changeEmailRequest, final Optional<User> user) {
		final var userDetails = user.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));

		log.info("Useremail {}: " + userDetails.getUseremail(),
				" ChangeEmailRequest {}: " + changeEmailRequest.getUseremail());

		if (!userDetails.getUseremail().equals(changeEmailRequest.getUseremail())
				|| changeEmailRequest.getUseremail().equals(changeEmailRequest.getNewUseremail())) {
			throw new AuthException(AuthErrorCode.INVALID_USER_EMAIL);
		}
		final var userCount = userDAO.countByUseremail(changeEmailRequest.getNewUseremail());
		if (userCount.intValue() > 0) {
			throw new AuthException(AuthErrorCode.USER_EXISTS_WITH_EMAIL);
		}
	}

	private String encodePassword(final String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public boolean matchPassword(final String password, final String encodedString) {
		return passwordEncoder.matches(password, encodedString);
	}

	@Override
	public UserTypeTO updateUserType(final UserTypeRequest userTypeRequest) {
		final var user = findByUserEmailAndUserType(userTypeRequest.getUseremail(), userTypeRequest.getUserType())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		final var userType = user.getUserTypes().stream()
				.filter(type -> type.getType().equals(userTypeRequest.getUserType())).findFirst()
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		userTypeRequestMapper.mapEntity(userTypeRequest, userType);
		return userDAO.updateUserType(userType);
	}

	public String generateJsonWebToken(final Map<String, String> data, final int expiryInDays) {
		return jwsTokenHandler.generateJWT(data, expiryInDays * 24 * 60, Constants.GUEST_USER);
	}

	@Override
	public ListResponse<AppUserTO> searchUser(final UserListRequest userListRequest) {
		validateSearchRequest(userListRequest);
		return userDAO.searchUser(userListRequest);
	}

	@Override
	public Long searchUserCount(final UserListRequest userListRequest) {
		validateSearchRequest(userListRequest);
		return userDAO.searchUserCount(userListRequest);
	}

	private void validateSearchRequest(final UserListRequest userListRequest) {
		userContextService.verifyAdminAccess();
		if (StringUtils.isBlank(userListRequest.getOrderBy()) || userListRequest.getSearchUserType() == null) {
			log.error("Invalid search request {}", userListRequest);
			throw new AuthException(AuthErrorCode.INVALID_SEARCH_REQUEST);
		}
	}

	@Override
	public void changeEmailHistory(final ChangeEmailRequest changeEmailRequest) {
		userDAO.changeEmailHistory(changeEmailRequest);
	}

	@Override
	public void updateUserLoginDetails(final AppUserTO userTO) {
		final var appUserTO = new AppUserTO();
		appUserTO.setUserId(userTO.getUserId());
		appUserTO.setLastLoginTimestamp(LocalDateTime.now());
		appUserTO.setLastAccessToken2("");
		appUserTO.setStaySignedIn(userTO.getStaySignedIn());
		if (userTO.getStaySignedIn().booleanValue()) {
			appUserTO.setLastAccessToken1(userTO.getAccessToken());
			appUserTO.setTokenExpiryTimestamp(userTO.getTokenExpiryTimestamp());
		} else {
			appUserTO.setLastAccessToken1("");
		}
		updateUser(appUserTO);
	}

	@Override
	@Transactional
	public int updateLastAccessToken(final Long userId, final String currentToken, final String newToken) {
		return userDAO.updateLastAccessToken(userId, currentToken, newToken);
	}

	@Override
	@Transactional
	public void bulkUpdateForUserDelete(final List<Long> userIds, final UserStatus status) {
		userDAO.bulkUpdateForUserDelete(userIds, status);
	}

	@Override
	public void bulkUpdateForUserUnDelete(final List<Long> userIds, final UserStatus status) {
		userRepository.bulkUpdateForUserUnDelete(userIds, status);

	}

	@Transactional
	@Override
	public AppUserTO addUserType(final UserTypeRequest request) {
		final var user = addUserType(request.getUseremail(), request.getUserType());
		userMetadataService.save(request.getCustomFields(), user.getUserId());
		final var userTypes = user.getUserTypes();
		user.setUserTypes(Set.of(filterUserTypeTO(userTypes, request.getUserType())
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND))));
		persistRolePrivileges(request.getRoleList(), request.getPrivilegeList(), user);
		user.setUserTypes(userTypes);
		return user;
	}

	/**
	 * <ul>
	 * <li>userIdentifier can be the user email or phone.</li>
	 * <li>Mark the method as @Transactional so that lazy loading of user types
	 * won't fail.
	 * https://stackoverflow.com/questions/22821695/how-to-fix-hibernate-lazyinitializationexception-failed-to-lazily-initialize-a</li>
	 * </ul>
	 */
	@Transactional
	@Override
	public void resetPasswordAndNotify(final String userIdentifier, final BulkResetPasswordRequest request) {
		try {
			NotificationType notificationType;
			String userEmail = null;
			String phoneNo = null;
			if (UserUtils.isValidEmail(userIdentifier)) {
				notificationType = NotificationType.EMAIL;
				userEmail = userIdentifier;
			} else {
				notificationType = NotificationType.SMS;
				phoneNo = userIdentifier;
			}
			final var userInfo = generateResetPasswordToken(userEmail, phoneNo, null);
			final var resetLink = generateResetPasswordLink(userInfo.getResetToken(), request.getResetLinkPrefix());
			notificationService.notifyResetPassword(request, notificationType, userInfo, resetLink);
			log.info("Successfully notified user {}", userIdentifier);
		} catch (final AuthException e) {
			// Log and continue
			log.error("Invalid idenfier {} {}", userIdentifier, e.getMessage());
		} catch (final Exception e) {
			// Log and continue
			log.error("Failed to process the user {}", userIdentifier, e);
		}
	}

	private String generateResetPasswordLink(final String token, final String resetLinkPrefix) {
		return resetLinkPrefix + token;
	}

	@Override
	public Optional<AppUserTO> getUserByEmailOrPhone(final String value) {
		if (UserUtils.isValidEmail(value)) {
			return getUserByEmail(value);
		}
		return getUserByPhone(value);
	}

	@Override
	public Optional<AppUserTO> getByIdAndType(final Long userId, final AuthUserType userType) {
		return getUserById(userId).filter(
				user -> filterUserTypeTO(user.getUserTypes(), userType).filter(UserTypeTO::isEnabled).isPresent());

	}

	@Override
	public List<AppUserTO> getByUserIdentifierList(final UserIdentifierSearchRequest request) {
		return userDAO.getByUserIdentifierList(request);
	}

	@Override
	public OTPResponse validatePhoneAndGenerateOTP(final OTPRequest request) {
		Long countByPhoneNo;
		if (request.getUserType() == null) {
			countByPhoneNo = userDAO.countByPhoneNo(request.getPhone());
		} else {
			countByPhoneNo = userDAO.countByPhoneNoAndUserType(request.getPhone(), request.getUserType());
		}
		if (countByPhoneNo == 0) {
			throw new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB);
		}
		if (countByPhoneNo > 1) {
			throw new AuthException(AuthErrorCode.PHONE_MULTIPLE_ACCOUNTS);
		}
		return generateOTP(request);
	}

	@Override
	public OTPResponse verifyPhoneAndGenerateOTP(final String phoneNo, final String locale) {
		userValidator.validatePhone(phoneNo);
		final var response = generateOTP(new OTPRequest(phoneNo, locale));
		getUserByPhone(phoneNo).ifPresent(data -> response.setUserTypes(data.getUserTypes()));
		return response;
	}

	@Override
	@Transactional
	public UserTypeTO deleteSecondaryUser(final UserTypeRequest request) {
		userContextService.verifyPrimaryContact();
		log.info("Inside user delete request {}", request);
		final var user = findByUserEmailAndType(request.getUseremail(), request.getUserType())
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
		final var userType = filterUserTypeTO(user.getUserTypes(), request.getUserType())
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND));
		verifyContactId(user.getCustomFields());
		log.info("Going to delete userTypeId {}", userType);
		if (user.getUserTypes().size() == 1) {
			nullifyUser(user);
		}
		final var count = userTypeDAO.deleteById(userType.getUserTypeId());
		log.info("UserType delete count {}", count);
		if (count > 0) {
			userMetadataService.deleteByCodeAndUserId(
					Maps.difference(request.getCustomFields(), MapUtils.emptyIfNull(user.getCustomFields()))
							.entriesInCommon().keySet(),
					user.getUserId());
		}
		return userType;
	}

	private void nullifyUser(final AppUserTO user) {
		final var appUser = new AppUserTO();
		appUser.setUserId(user.getUserId());
		appUser.setStatus(UserStatus.DELETED);
		appUser.setUseremail(String.join("_", user.getUserId().toString(), user.getUseremail()));
		appUser.setUserName(appUser.getUseremail());
		appUser.setPhoneNo(user.getUserId().toString());
		appUser.setClientId(StringUtils.EMPTY);
		appUser.setClientSecretHash(StringUtils.EMPTY);
		appUser.setUserSecretHash(StringUtils.EMPTY);
		nullifySocialDetails(appUser);
		updateUser(appUser);
	}

	private void verifyContactId(final Map<String, String> customFields) {
		final var primaryContactId = customFields.getOrDefault(UserCustomField.PRIMARY_CONTACT_ID.getName(),
				StringUtils.EMPTY);
		final var contactId = customFields.getOrDefault(UserCustomField.CONTACT_ID.getName(), StringUtils.EMPTY);
		if (isPrimaryContact(primaryContactId, contactId)
				|| !primaryContactId.equals(userContextService.getCustomField(UserCustomField.CONTACT_ID))) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	private boolean isPrimaryContact(final String primaryContactId, final String contactId) {
		return primaryContactId.equals(contactId);
	}

	@Override
	public Optional<Map<String, String>> getUserOtpByEmailOrPhone(final String value) {
		ApiException.ifThrow(
				!accessUserOtp || !userContextService.isAdminLogin()
						|| !userContextService.hasPrivilege("USER_MANAGEMENT:OTP_ACCESS"),
				AuthErrorCode.UNAUTHORIZED_ACCESS);
		return Booleans
				.getIfTrueOrElse(UserUtils.isValidEmail(value), () -> otpHandler.findRecentOtp(null, value),
						() -> otpHandler.findRecentOtp(value, null))
				.map(otpvalue -> Map.of(Constants.OTP_FIELD, otpvalue));
	}

	@Override
	public List<AppUserTO> searchUserByPrivilege(final String privilegeCode, final String accessCode) {
		userContextService.verifyAdminAccess();
		return userDAO.findUserByPrivilege(privilegeCode, accessCode);
	}

	@Override
	public UserLoginAttemptVO executeLoginAttempts(final UserLoginAttemptVO loginAttemptVO) {
		return userDAO.executeLoginAttempts(loginAttemptVO);
	}
}
