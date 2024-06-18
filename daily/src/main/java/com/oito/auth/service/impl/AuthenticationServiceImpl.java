package com.oito.auth.service.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.StaySignedInRequest;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.dao.PrivilegeDAO;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.dao.UserTypeDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.AppUserToUserMapper;
import com.oito.auth.service.AuthenticationService;
import com.oito.auth.service.ClientService;
import com.oito.auth.service.UserMetadataService;
import com.oito.auth.service.UserService;
import com.oito.auth.service.UserTokenService;
import com.oito.auth.util.MaskUtils;
import com.oito.auth.v1.common.to.AppUserTOV1;
import com.oito.auth.v1.mapper.AppUserTOV1Mapper;
import com.oito.auth.v1.mapper.OTPLoginMapper;
import com.oito.auth.validator.UserValidator;
import com.oito.common.auth.jwt.JWETokenHandler;
import com.oito.common.auth.jwt.TokenType;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic User Service implementation
 *
 * @author Dileep
 *
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private UserMetadataService metadataService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserTypeDAO userTypeDAO;

	@Value("${oito.jwt.expiry-in-minutes}")
	private int tokenExpiryInMinutes;

	@Value("${oito.jwt.custom.expiry-in-days}")
	private int customExpiryInDays;

	@Value("${oito.login.verification-required: false}")
	private boolean loginVerificationRequired;

	/**
	 * Using this provider to decode the access token to get userid
	 */

	@Autowired
	private UserService userService;
	
	@Autowired
	private ClientService clientService;

	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private JWETokenHandler jweTokenHandler;

	@Autowired
	private PrivilegeDAO privilegeDAO;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private AppUserToUserMapper userMapper;

	@Autowired
	private UserTokenService userTokenService;

	@Autowired
	private OTPLoginMapper otpLoginMapper;

	@Autowired
	private AppUserTOV1Mapper appUserToMobileUsertOMapper;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.auth.service.AuthenticationService#authenticateUsernamePassword(com.auth.
	 * common.to.AppUserTO)
	 */
	@Override
	@Transactional
	public AppUserTO authenticateUsernamePassword(final UserLoginRequest loginRequest, final String sessionId,
			final boolean enforceVerification) {
		userService.populatePhoneNumber(loginRequest);
		userValidator.validateLoginRequest(loginRequest);
		final var isEmailChanged = replaceFirstNonASCIICharacterInEmail(loginRequest);
		final var userTO = userService.getUserForLogin(loginRequest);

		// TODO Remove userTO.getUserSecretHash() == null check once all the users have
		// password
		if (!userTO.getSocialFlag() && null == userTO.getUserSecretHash()
				&& userService.filterUserTypeTO(userTO.getUserTypes(), loginRequest.getUserType()).isPresent()) {
			log.info("User password is not stored for the user: {}", userTO.getUseremail());
			throw new AuthException(AuthErrorCode.LOGIN_RESET_PASSWORD_ENFORCE);
		}
		validateUser(loginRequest, userTO, enforceVerification);
		if (StringUtils.isNotBlank(userTO.getError())) {
			return userTO;
		}
		userTO.setStaySignedIn(BooleanUtils.toBoolean(loginRequest.getStaySignedIn()));
		userTO.setUserType(loginRequest.getUserType());
		userTO.setTokenParams(loginRequest.getTokenParams());
		generateRefreshToken(loginRequest, sessionId, userTO);
		postLogin(userTO, loginRequest.getUserType(), sessionId, AuthProvider.LOCAL);
		userValidator.populateUserWarnings(userTO, isEmailChanged);
		return userTO;
	}
	
	@Override
	@Transactional
	public AppUserTO authenticateUsernamePasswordWithPartner(final UserLoginRequest loginRequest, final String sessionId,
			final boolean enforceVerification) {
		userService.populatePhoneNumber(loginRequest);
		userValidator.validateLoginRequest(loginRequest);
		final var isEmailChanged = replaceFirstNonASCIICharacterInEmail(loginRequest);
		final var userTO = userService.getUserForLogin(loginRequest);

		if (!userTO.getSocialFlag() && null == userTO.getUserSecretHash()
				&& userService.filterUserTypeTO(userTO.getUserTypes(), loginRequest.getUserType()).isPresent()) {
			log.info("User password is not stored for the user: {}", userTO.getUseremail());
			throw new AuthException(AuthErrorCode.LOGIN_RESET_PASSWORD_ENFORCE);
		}
		validateUser(loginRequest, userTO, enforceVerification);
		if (StringUtils.isNotBlank(userTO.getError())) {
			return userTO;
		}
		
		userTO.setCustomFields(fetchCustomFieldsForClient(loginRequest.getClientId(), loginRequest.getSecretKey()));
		userTO.setStaySignedIn(BooleanUtils.toBoolean(loginRequest.getStaySignedIn()));
		userTO.setUserType(loginRequest.getUserType());
		userTO.setTokenParams(loginRequest.getTokenParams());
		generateRefreshToken(loginRequest, sessionId, userTO);
		postLogin(userTO, loginRequest.getUserType(), sessionId, AuthProvider.LOCAL);
		userValidator.populateUserWarnings(userTO, isEmailChanged);
		return userTO;
	}
	
	@Override
	@Transactional
	public AppUserTO authenticateClientGuestLogin(final UserLoginRequest loginRequest) {
		final var userTO = AppUserTO.empty();
		userTO.setCustomFields(fetchCustomFieldsForClient(loginRequest.getClientId(), loginRequest.getSecretKey()));
		populateExpiryTime(userTO.getCustomFields());
		generateAccessToken(userTO, TokenType.GUEST, AuthUserType.BUYER,
				userTO.getCustomFields(), List.of(), Instant.now().plus(customExpiryInDays, ChronoUnit.DAYS));
		return userTO;
	}
	
	private void populateExpiryTime(final Map<String, String> customFields) {
		customFields.put("expiryTime", Long.toString(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));
	}
	
	private Map<String, String> fetchCustomFieldsForClient(String clientID, String secretKey) {
		log.info("Custom Fields fetch with Client ID : {}", clientID);
		final var user = clientService.validateClientCredentials(clientID, secretKey);
		
		return metadataService.findUserMetadataMap(user.getUserId());
	}

	private void generateRefreshToken(final UserLoginRequest loginRequest, final String sessionId,
			final AppUserTO userTO) {
		if (loginRequest.isGenerateRefreshToken()) {
			final var token = userTokenService.postLoginRefreshToken(
					requireNonNullElse(loginRequest.getMacId(), sessionId), userTO.getUserId(),
					loginRequest.getUserType());
			if (nonNull(token) && nonNull(token.getUserTokenId())) {
				userTO.setRefreshToken(token.getToken());
				final var tokenParams = userTO.getTokenParams();
				final var tokenId = token.getUserTokenId().toString();
				if (isNull(tokenParams)) {
					userTO.setTokenParams(Map.of(Constants.REFRESH_TOKEN_KEY, tokenId));
				} else {
					tokenParams.put(Constants.REFRESH_TOKEN_KEY, tokenId);
				}
			}
		}
	}

	private boolean replaceFirstNonASCIICharacterInEmail(final UserLoginRequest request) {
		final var email = request.getUseremail();
		if (StringUtils.isEmpty(email)) {
			return false;
		}
		request.setUseremail(userValidator.replaceFirstNonASCIICharacter(request.getUseremail()));
		return !email.equals(request.getUseremail());
	}

	@Override
	public AppUserTO impersonate(final UserLoginRequest requestData, final String sessionId) {
		userValidator.validateLoginRequest(requestData);
		userContextService.verifyAdminAccess();
		final var currentUser = userService.getUserByEmail(requestData.getUseremail())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		if (userService.filterUserTypeTO(currentUser.getUserTypes(), requestData.getUserType()).isEmpty()) {
			throw new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND);
		}
		validateUserSecretHash(currentUser, requestData.getPassword());

		final var impersontatedUser = userService.getUserByEmail(requestData.getImpersonateUserEmail())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		validateImpersonatedUser(impersontatedUser);
		postLogin(impersontatedUser, requestData.getUserType(), sessionId, AuthProvider.LOCAL);
		impersontatedUser.setImpersonateUserEmail(requestData.getImpersonateUserEmail());
		return impersontatedUser;
	}

	private void validateImpersonatedUser(final AppUserTO impersontatedUser) {
		if (metadataService.isAdmin(impersontatedUser.getCustomFields())) {
			throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
		}
	}

	@Override
	@SneakyThrows
	public AppUserTO staySignedIn(final StaySignedInRequest requestData) {
		var appUser = userDao.findById(requestData.getUserId()).filter(AppUserTO::getStaySignedIn)
				.filter(user -> requestData.getLastAccessToken().equals(user.getLastAccessToken1())
						|| requestData.getLastAccessToken().equals(user.getLastAccessToken2()))
				.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
		log.info("StaySignedIn data from db {}", appUser);

		generateStaySignedInAccessToken(appUser, requestData.getUserType(), appUser.getTokenExpiryTimestamp());
		final var updatedCount = userService.updateLastAccessToken(requestData.getUserId(),
				requestData.getLastAccessToken(), appUser.getAccessToken());
		if (0 == updatedCount) {
			appUser = userDao.findById(requestData.getUserId()).filter(AppUserTO::getStaySignedIn)
					.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN));
			if (!requestData.getLastAccessToken().equals(appUser.getLastAccessToken2())) {
				throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
			}
			log.info("StaySignedIn Count zero {}", appUser);
		} else if (!requestData.getLastAccessToken().equals(appUser.getLastAccessToken1())) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		} else {
			appUser.setLastAccessToken2(appUser.getLastAccessToken1());
			appUser.setLastAccessToken1(appUser.getAccessToken());
		}
		appUser.setCustomFields(metadataService.findUserMetadataMap(appUser.getUserId()));
		appUser.setPrivilegeList(privilegeDAO.getUserPrivileges(appUser.getUserId()));
		generateIdToken(appUser);
		return appUser;
	}

	private void validateUser(final UserLoginRequest request, final AppUserTO user, final boolean enforceVerification) {
		if (enforceVerification) {
			validateVerificationStatus(user);
		}
		validateUserSecretHash(user, request.getPassword());
		validatePortalUser(request, user);
		validateUserStatus(user);
	}

	private void validateVerificationStatus(final AppUserTO user) {
		if (loginVerificationRequired && BooleanUtils.isFalse(user.getVerified())) {
			setError(user, AuthErrorCode.UN_VERIFIED_USER);
		}
	}

	private void validateUserStatus(final AppUserTO user) {
		if (user.getStatus().equals(UserStatus.DELETED)) {
			setError(user, AuthErrorCode.DELETED_USER);
		}
	}

	private void validatePortalUser(final UserLoginRequest request, final AppUserTO user) {
		final var userTypeTO = userService.filterUserTypeTO(user.getUserTypes(), request.getUserType());
		if (userTypeTO.isEmpty()) {
			if (StringUtils.isNotBlank(request.getUseremail())) {
				setError(user, AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_EMAIL);
			} else if (StringUtils.isNotBlank(request.getUserName())) {
				setError(user, AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_USERNAME);
			} else {
				user.setMaskedUserEmail(MaskUtils.maskEmailAddress(user.getUseremail(), '*'));
				user.setUseremail(null);
				setError(user, AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_PHONE);
			}
		} else if (!userTypeTO.get().isEnabled()) {
			setError(user, AuthErrorCode.USER_DISABLED);
		}
	}

	private void setError(final AppUserTO user, final AuthErrorCode error) {
		user.setError(error.getErrorMessage());
		user.setErrorCode(error.name());
		user.setErrorAction(error.getErrorAction());
	}

	@Override
	public void postLogin(final AppUserTO userTO, final AuthUserType userType, final String sessionId,
			final AuthProvider authProvider) {
		userTO.setPrivilegeList(privilegeDAO.getUserPrivileges(userTO.getUserId()));
		if (MapUtils.isEmpty(userTO.getCustomFields())) {
			userTO.setCustomFields(metadataService.findUserMetadataMap(userTO.getUserId()));
		}
		userTO.setStaySignedIn(isStaySignedInRequest(userTO));
		populateCustomTokenInfo(userTO, userType);
		userService.saveLoginHistory(userTO.getUserId(), sessionId, authProvider);
		userTO.setSessionId(sessionId);
		userService.updateUserLoginDetails(userTO);
	}

	private void validateUserSecretHash(final AppUserTO userTO, final String password) {
		if (null == userTO.getUserSecretHash() || !userService.matchPassword(password, userTO.getUserSecretHash())) {
			throw new AuthException(AuthErrorCode.IDP_API_EXCEPTION);
		}
		log.info("Internal user validation completed: {}", userTO.getUseremail());
	}

	private void populateCustomTokenInfo(final AppUserTO userTo, final AuthUserType userType) {
		generateAccessToken(userTo, userType);
		generateIdToken(userTo);
	}

	private void generateIdToken(final AppUserTO userTo) {
		userTo.setIdToken(jweTokenHandler.generateJWT(userTo.getCustomFields(), userTo.getPrivilegeList(),
				tokenExpiryInMinutes, userTo.getUserName()));
	}

	private void generateAccessToken(final AppUserTO userTo, final AuthUserType userType) {
		if (userTo.getStaySignedIn().booleanValue()) {
			generateStaySignedInAccessToken(userTo, userType);
		} else {
			if (MapUtils.isEmpty(userTo.getCustomFields())) {
				generateAccessToken(userTo, TokenType.USER, userType, new HashMap<>());
			} else {
				generateAccessToken(userTo, TokenType.USER, userType, userTo.getCustomFields());
			}
		}
	}

	private boolean isStaySignedInRequest(final AppUserTO userTo) {
		return userTo.getStaySignedIn().booleanValue() && !metadataService.isAdmin(userTo.getCustomFields());
	}

	private void generateAccessToken(final AppUserTO userTo, final TokenType tokenType, final AuthUserType userType,
			final Map<String, String> customFields) {
		generateAccessToken(userTo, tokenType, userType, customFields, tokenExpiryInMinutes);
	}

	private void generateStaySignedInAccessToken(final AppUserTO userTo, final AuthUserType userType) {
		userTo.setTokenExpiryTimestamp(LocalDateTime.now().plusDays(customExpiryInDays));
		log.info("Custom Expiry In Days {} {}", customExpiryInDays, userTo.getTokenExpiryTimestamp());
		generateStaySignedInAccessToken(userTo, userType, userTo.getTokenExpiryTimestamp());
	}

	private void generateStaySignedInAccessToken(final AppUserTO userTo, final AuthUserType userType,
			final LocalDateTime expiryDate) {
		final Map<String, String> customFields = new HashMap<>();
		final var internalExpiry = plusMinutes(tokenExpiryInMinutes);

		customFields.put("expiryTime", Long.toString(internalExpiry.toEpochMilli()));
		generateAccessToken(userTo, TokenType.STAY_SIGNED_IN, userType, customFields, List.of(),
				expiryDate.toInstant(ZoneOffset.UTC));
		log.info("Token{} Expiry Time{}", userTo.getAccessToken(), internalExpiry);
	}

	private Instant plusMinutes(final int expiryInMinutes) {
		return LocalDateTime.now().plusMinutes(expiryInMinutes).toInstant(ZoneOffset.UTC);
	}

	private void generateAccessToken(final AppUserTO userTo, final TokenType tokenType, final AuthUserType userType,
			final Map<String, String> customFields, final int expiryInMinutes) {
		generateAccessToken(userTo, tokenType, userType, customFields, List.of(), expiryInMinutes);
	}

	@Override
	public void generateAccessToken(final AppUserTO userTo, final TokenType tokenType, final AuthUserType userType,
			final Map<String, String> customFields, final List<String> audiences, final int expiryInMinutes) {
		generateAccessToken(userTo, tokenType, userType, customFields, audiences, plusMinutes(expiryInMinutes));
	}

	private void generateAccessToken(final AppUserTO userTo, final TokenType tokenType, final AuthUserType userType,
			final Map<String, String> customFields, final List<String> audiences, final Instant expirtyTime) {
		populateTokenCustomFields(userTo, tokenType, userType, customFields);
		userTo.setAccessToken(jweTokenHandler.generateJWT(customFields, audiences, expirtyTime, userTo.getUserName()));
		userTo.setTokenExpiryTimestamp(LocalDateTime.ofInstant(expirtyTime, ZoneId.systemDefault()));
	}

	private void populateTokenCustomFields(final AppUserTO userTo, final TokenType tokenType,
			final AuthUserType userType, final Map<String, String> customFields) {
		customFields.put(Constants.TOKEN_TYPE_KEY, tokenType.name());
		customFields.put("userId", userTo.getUserId().toString());
		customFields.put(Constants.USER_TYPE_KEY, userType.name());
		customFields.putAll(MapUtils.emptyIfNull(userTo.getTokenParams()));
	}

	@Override
	public AppUserTO loginWithPhone(final OTPPhoneVerificationRequest requestData, final String sessionId) {
		return loginWithPhone(otpLoginMapper.map(requestData), sessionId);
	}

	@Override
	public AppUserTO loginWithPhone(final UserLoginRequest requestData, final String sessionId) {
		final var user = userService.verifyOTPAndValidatePhone(requestData);
		if (user == null) {
			throw new AuthException(AuthErrorCode.OTP_NOT_FOUND_OR_EXPIRED);
		}
		final var appUserTO = userMapper.toVO(user);
		appUserTO.setPhoneVerified(Boolean.TRUE);
		generateRefreshToken(requestData, sessionId, appUserTO);
		postLogin(appUserTO, requestData.getUserType(), sessionId, AuthProvider.LOCAL);
		return appUserTO;
	}

	@Override
	public AppUserTOV1 getValidRefreshToken(final String token, final String sessionId) {
		final var userTokenTO = userTokenService.getValidRefreshToken(token);
		final var userTypeTO = userTypeDAO.findById(userTokenTO.getUserTypeId());
		final var appUserTO = userDao.findById(userTypeTO.getUserId())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS));
		postLogin(appUserTO, userTypeTO.getType(), sessionId, AuthProvider.LOCAL);
		return appUserToMobileUsertOMapper.appAndTokenTO(appUserTO, userTokenTO);
	}

}
