package com.oito.auth.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.LiffLoginRequest;
import com.oito.auth.common.to.OTPLineVerificationRequest;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.config.LineConfigProperties;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.UserSignUpRequestMapper;
import com.oito.auth.otp.OTPHandler;
import com.oito.auth.proxy.LiffClientProxy;
import com.oito.auth.proxy.NotificationServiceProxy;
import com.oito.auth.service.AuthenticationService;
import com.oito.auth.service.SocialUserService;
import com.oito.auth.service.UserMetadataService;
import com.oito.auth.service.UserService;
import com.oito.common.auth.jwt.JWETokenHandler;
import com.oito.common.exception.ApiException;
import com.oito.common.exception.errorcode.ErrorCode;

import feign.FeignException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SocialLoginServiceImpl implements SocialUserService {

	@Autowired
	private UserService userService;

	@Autowired
	private UserDAO userDao;

	@Autowired
	private AuthenticationService authService;

	@Autowired
	private UserMetadataService userMetadataService;

	@Autowired
	private LiffClientProxy liffProxy;

	@Autowired
	private LineConfigProperties lineConfiguration;

	@Value("${oito.social.signup-login.supported-usertypes:}")
	private List<AuthUserType> supportedUserTypeList;

	@Value("${oito.phone.mandatory:#{true}}")
	private boolean isPhoneMandatory;

	@Autowired
	private NotificationServiceProxy notificationProxy;

	@Autowired
	private UserSignUpRequestMapper mapper;

	@Autowired
	private JWETokenHandler tokenHandler;

	@Autowired
	private OTPHandler otpHandler;

	@Override
	@Transactional
	public AppUserTO login(final SocialLoginRequest loginRequest, final String sessionId) {
		final var user = findBySocialId(loginRequest.getSocialId(), loginRequest.getAuthProvider())
				.orElseGet(() -> userService.getUserByEmail(loginRequest.getUseremail())
						.orElseGet(() -> getUserById(loginRequest.getUserId()).orElse(null)));
		if (user == null) {
			return loginAction(loginRequest, sessionId);
		}
		return validateAndPostProcessLogin(loginRequest, user, sessionId);
	}

	private AppUserTO loginAction(final SocialLoginRequest loginRequest, final String sessionId) {
		if (isLogin(loginRequest.getUserType())) {
			return AppUserTO.empty();
		}
		return signUpAndLogin(loginRequest, sessionId);
	}

	private AppUserTO signUpAndLogin(final SocialLoginRequest loginRequest, final String sessionId) {
		final var signUpRequest = mapper.toEntity(loginRequest);
		log.info("Auto signup through login {} {}", loginRequest, signUpRequest);
		final var user = socialSignUp(signUpRequest, sessionId);
		processPostLogin(loginRequest, sessionId, user);
		return user;
	}

	private AppUserTO validateAndPostProcessLogin(final SocialLoginRequest loginRequest, @NonNull final AppUserTO user,
			final String sessionId) {
		return invalidUser(loginRequest, user).orElseGet(() -> processPostLogin(loginRequest, sessionId, user));
	}

	private boolean isLogin(final AuthUserType userType) {
		return isPhoneMandatory || !supportedUserTypeList.contains(userType);
	}

	private Optional<AppUserTO> getUserById(final Long userId) {
		return userId == null ? Optional.empty() : userService.getUserById(userId);
	}

	@Override
	@Transactional
	public AppUserTO signUp(final UserSignUpRequest requestData, final String sessionId) {
		setSocialData(requestData);
		final var user = userService.getUserByEmail(requestData.getUseremail())
				.orElseGet(() -> userService.getUserByPhone(requestData.getPhoneNo()).orElse(null));
		if (user != null) {
			return handleExistingUsers(requestData, sessionId, user);
		}
		return socialSignUp(requestData, sessionId);
	}

	private AppUserTO socialSignUp(final UserSignUpRequest requestData, final String sessionId) {
		setSocialData(requestData);
		final var user = userService.signUpUser(requestData);
		if (null != user.getSignupToken()) {
			authService.postLogin(user, requestData.getUserType(), sessionId, requestData.getAuthProvider());
		}
		return user;
	}

	private AppUserTO handleExistingUsers(final UserSignUpRequest requestData, final String sessionId,
			final AppUserTO user) {
		if (userService.isUserTypeExists(user, requestData.getUserType())) {
			if (isAccountLinkRequest(requestData, user)) {
				return linkSocialAccount(requestData, user, sessionId);
			}
			validateSameUser(requestData, user);
		}
		validateSamePortalUser(requestData, user);
		userService.addUserType(user, requestData.getUserType());
		userMetadataService.merge(requestData.getCustomFields(), user.getUserId());
		authService.postLogin(user, requestData.getUserType(), sessionId, requestData.getAuthProvider());
		return user;
	}

	private void validateSamePortalUser(final UserSignUpRequest requestData, final AppUserTO user) {
		if (isSamePhoneNO(requestData, user)) {
			throw new AuthException(AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_PHONE);
		}
		if (isSameEmail(requestData, user)) {
			throw new AuthException(AuthErrorCode.OTHER_PORTAL_USER_EXISTS_WITH_EMAIL);
		}
	}

	private void validateSameUser(final UserSignUpRequest requestData, final AppUserTO user) {
		if (isSamePhoneNO(requestData, user)) {
			throw new AuthException(AuthErrorCode.USER_EXISTS_WITH_PHONE);
		}
		if (isSameEmail(requestData, user)) {
			throw new AuthException(AuthErrorCode.USER_EXISTS_WITH_EMAIL);
		}
	}

	private boolean isSameEmail(final UserSignUpRequest requestData, final AppUserTO user) {
		return Objects.equals(requestData.getUseremail(), user.getUseremail())
				&& (StringUtils.isNotBlank(requestData.getPhoneNo())
						&& !Objects.equals(requestData.getPhoneNo(), user.getPhoneNo()));
	}

	private boolean isSamePhoneNO(final UserSignUpRequest requestData, final AppUserTO user) {
		return Objects.equals(requestData.getPhoneNo(), user.getPhoneNo())
				&& !Objects.equals(requestData.getUseremail(), user.getUseremail());
	}

	/**
	 * Link the social account to existing user account
	 *
	 * @param requestData
	 * @param user
	 * @param sessionId
	 * @return
	 */
	private AppUserTO linkSocialAccount(final UserSignUpRequest requestData, final AppUserTO user,
			final String sessionId) {
		return login(SocialLoginRequest.fromAccountLinkRequest(requestData, user), sessionId);
	}

	/**
	 * Check if the user already exists and we're trying to link the social account
	 * to it
	 *
	 * @param requestData
	 * @param user
	 * @return
	 */
	private boolean isAccountLinkRequest(final UserSignUpRequest requestData, final AppUserTO user) {
		return (AuthProvider.FACEBOOK.equals(requestData.getAuthProvider()) && user.getFacebookId() == null)
				|| (AuthProvider.GOOGLE.equals(requestData.getAuthProvider()) && user.getGoogleId() == null)
				|| (AuthProvider.LINE.equals(requestData.getAuthProvider()) && user.getLineId() == null);
	}

	private void setSocialData(final UserSignUpRequest requestData) {
		if (AuthProvider.GOOGLE == requestData.getAuthProvider()) {
			requestData.setGoogleId(requestData.getSocialId());
		} else if (AuthProvider.FACEBOOK == requestData.getAuthProvider()) {
			requestData.setFacebookId(requestData.getSocialId());
		} else if (AuthProvider.LINE == requestData.getAuthProvider()) {
			requestData.setLineId(requestData.getSocialId());
			handleCommunicationChannel(requestData);
		}
		requestData.setSocialFlag(true);
	}

	private void handleCommunicationChannel(final UserSignUpRequest requestData) {
		if (StringUtils.isNotBlank(requestData.getLineId())) {
			return;
		}
		/*
		 * During sign up, if user is a friend of line Official account, add LINE as a
		 * preferred channel
		 */
		if (!requestData.getCommunicationChannels().contains(CommunicationChannel.LINE)
				&& notificationProxy.isLineBotFollower(requestData.getLineId())) {
			requestData.getCommunicationChannels().add(CommunicationChannel.LINE);
		}
	}

	/**
	 * Validates the user
	 *
	 * @param loginRequest
	 * @param user
	 * @return
	 */
	private Optional<AppUserTO> invalidUser(final SocialLoginRequest loginRequest, final AppUserTO user) {
		if (isDeleted(user) || isDisabled(loginRequest.getUserType(), user)) {
			final var invalidUser = AppUserTO.empty();
			setSocialError(invalidUser, AuthErrorCode.USER_DISABLED);
			return Optional.of(invalidUser);
		}
		return Optional.empty();
	}

	/**
	 * Checks whether the user is disabled
	 *
	 * @param loginRequest
	 * @param user
	 * @return
	 */
	private boolean isDisabled(final AuthUserType userType, final AppUserTO user) {
		return userService.filterUserTypeTO(user.getUserTypes(), userType).filter(userTypeTO -> !userTypeTO.isEnabled())
				.isPresent();
	}

	/**
	 * Checks whether the user is deleted
	 *
	 * @param user
	 * @return
	 */
	private boolean isDeleted(final AppUserTO user) {
		return UserStatus.DELETED == user.getStatus();
	}

	/**
	 * Sets social error details to the user
	 *
	 * @param user
	 * @param error
	 */
	private void setSocialError(final AppUserTO user, final AuthErrorCode error) {
		user.setError(error.getErrorMessage());
		user.setErrorCode(error.name());
		user.setSocialErrorCode(error.name());
		user.setErrorAction(error.getErrorAction());
	}

	/**
	 * Performs post login actions
	 *
	 * @param loginRequest
	 * @param sessionId
	 * @param user
	 * @return
	 */
	private AppUserTO processPostLogin(final SocialLoginRequest loginRequest, final String sessionId,
			final AppUserTO user) {
		updateSocialInfo(user, loginRequest);
		user.setStaySignedIn(loginRequest.getStaySignedIn());
		postLogin(loginRequest.getUserType(), loginRequest.getAuthProvider(), sessionId, user);
		return user;
	}

	private void postLogin(final AuthUserType authUserType, final AuthProvider authProvider, final String sessionId,
			final AppUserTO user) {
		if (null != user.getUserTypes() && userService.isUserTypeExists(user, authUserType)) {
			authService.postLogin(user, authUserType, sessionId, authProvider);
		}
	}

	@Override
	public AppUserTO loginWithLiff(final LiffLoginRequest requestData, final String sessionId) {
		final var lineId = extractLineId(requestData.getIdToken(), requestData.getUserType());
		final var user = findBySocialId(lineId, AuthProvider.LINE)
				.orElseThrow(() -> new AuthException(AuthErrorCode.LINE_ID_NOT_FOUND));
		validateUserType(user, requestData.getUserType());
		postLogin(requestData.getUserType(), AuthProvider.LINE, sessionId, user);
		return user;
	}

	private String extractLineId(final String idToken, final AuthUserType userType) {
		final var response = verifyLiffIdToken(idToken, userType);
		final var lineId = response.get("sub");
		if (lineId == null) {
			throw new AuthException(AuthErrorCode.INVALID_TOKEN);
		}
		return lineId.toString();
	}

	private Map<String, Object> verifyLiffIdToken(final String idToken, final AuthUserType userType) {
		try {
			final Map<String, String> requestMap = new HashMap<>();
			requestMap.put("client_id", lineConfiguration.getClientId().get(userType));
			requestMap.put("id_token", idToken);
			final var response = liffProxy.verifyIdToken(requestMap);
			log.info("response {}", response);
			return response;
		} catch (final FeignException fe) {
			log.error("LiffIdToken Extraction failed", fe);
			throw new AuthException(AuthErrorCode.INVALID_TOKEN, fe);
		}
	}

	private void validateUserType(final AppUserTO user, final AuthUserType userType) {
		final var userTypeTO = userService.filterUserTypeTO(user.getUserTypes(), userType)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_TYPE_NOT_FOUND));
		ApiException.ifThrow(!userTypeTO.isEnabled(), AuthErrorCode.USER_DISABLED);
	}

	@Override
	@Transactional
	public boolean verifyLineOtp(final OTPLineVerificationRequest request) {
		final var user = userService.findByPhoneNoAndUserType(request.getPhoneNo(), request.getUserType())
				.orElseThrow(() -> new AuthException(AuthErrorCode.UNABLE_TO_GET_USER_FROM_DB));
		final var isOtpVerified = otpHandler.verifyOTP(request.getOtpId().longValue(), request.getOtp(),
				request.getPhoneNo());
		if (isOtpVerified) {
			userDao.updateLineSocial(user.getUserId(), extractLineId(request.getIdToken(), request.getUserType()));
		}
		return isOtpVerified;
	}

	@Override
	public Map<String, String> extractToken(final String token) {
		final var tokenClaims = tokenHandler.decodeToken(token);
		if (!Constants.SOCIAL_USER_TOKEN.equals(tokenClaims.get("sub"))) {
			throw new ApiException(ErrorCode.INVALID_TOKEN);
		}
		return tokenClaims;
	}

	@Override
	public Optional<AppUserTO> findBySocialId(final String socialId, final AuthProvider provider) {
		return userDao.findBySocialId(socialId, provider);
	}

	@Override
	public void updateSocialInfo(final AppUserTO userTO, final SocialLoginRequest loginRequest) {
		final var addChannelRequired = checkAddLineChannelRequired(userTO, loginRequest);
		userDao.updateSocialInfo(userTO, loginRequest);
		if (addChannelRequired) {
			final var communicationChannels = userTO.getCommunicationChannels();
			communicationChannels.add(CommunicationChannel.LINE);
			userDao.updateCommunicationChannel(userTO.getUserId(), communicationChannels);
		}
	}

	private boolean checkAddLineChannelRequired(final AppUserTO userTO, final SocialLoginRequest loginRequest) {
		if (!loginRequest.getAuthProvider().equals(AuthProvider.LINE)
				|| userTO.getCommunicationChannels().contains(CommunicationChannel.LINE)) {
			return false;
		}
		// Line is added as a friend during the login
		if (loginRequest.isFriendShipStatusChanged()) {
			return true;
		}
		// First time login with line and user is already a friend
		return userTO.getLineId() == null && notificationProxy.isLineBotFollower(loginRequest.getSocialId());
	}

}
