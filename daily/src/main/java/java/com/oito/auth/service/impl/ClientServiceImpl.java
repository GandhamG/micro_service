package com.oito.auth.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ClientTO;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.mapper.ClientTOClientResponseMapper;
import com.oito.auth.service.AuthenticationService;
import com.oito.auth.service.ClientService;
import com.oito.auth.service.PrivilegeService;
import com.oito.auth.service.UserMetadataService;
import com.oito.auth.service.UserService;
import com.oito.auth.web.bean.ClientLoginResponse;
import com.oito.auth.web.bean.ClientResponse;
import com.oito.common.auth.jwt.TokenType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

	@Autowired
	private UserService userService;

	@Autowired
	private PrivilegeService privilegeService;

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ClientTOClientResponseMapper clientMapper;

	@Autowired
	private UserMetadataService userMetadataService;

	@Value("${oito.username.enable-username:#{false}}")
	private boolean enableUserName;

	@Value("${oito.jwt.client.expiry-in-days:1}")
	private int customTokenExpiryInDays;

	@Value("${oito.jwt.expiry-in-minutes:1440}")
	private int tokenExpiryInMinutes;

	@Autowired
	private UserDAO userDao;

	@Override
	@Transactional
	public ClientResponse create(final ClientTO clientTO) {
		log.info("Client Request {}", clientTO);
		userService.validateDuplicateUser(clientTO.getUserName(), clientTO.getUseremail(), clientTO.getPhoneNo());
		if (!enableUserName && StringUtils.isBlank(clientTO.getUserName())) {
			clientTO.setUserName(clientTO.getUseremail());
		}
		clientTO.setClientId(UUID.randomUUID().toString());
		clientTO.setSecretKey(UUID.randomUUID().toString());
		final var userTO = userService.saveClientUser(clientTO);
		updateCommonClientMetadata(clientTO, userTO);
		final var clientResponse = clientMapper.toVO(clientTO);
		clientResponse.setUserId(userTO.getUserId());
		return clientResponse;
	}

	private void updateCommonClientMetadata(final ClientTO clientTO, final AppUserTO userBean) {
		var clientMetadata = clientTO.getClientMetadata();
		if (MapUtils.isEmpty(clientMetadata)) {
			clientMetadata = new HashMap<>();
			clientTO.setClientMetadata(clientMetadata);
		}
		clientMetadata.put("userId", userBean.getUserId().toString());
		clientMetadata.put("userEmail", userBean.getUseremail());
		clientMetadata.put("userName", userBean.getUserName());
		if (null != clientTO.getTokenExpiryInMinutes()) {
			clientMetadata.put(Constants.TOKEN_EXPIRY_IN_MINUTES, clientTO.getTokenExpiryInMinutes().toString());
		}
		userMetadataService.save(clientMetadata, userBean.getUserId());

	}

	@Override
	public ClientLoginResponse login(final ClientTO clientLogin) {
		try {
			log.info("Token Creation {}", clientLogin);
			
			final var user = validateClientCredentials(clientLogin.getClientId(), clientLogin.getSecretKey());
			return populateCustomTokenInfo(user,
					user.getUserTypes().stream().findFirst()
							.orElseThrow(() -> new AuthException(AuthErrorCode.APP_USER_TYPE_EMPTY)).getType(),
					TokenType.CLIENT, tokenExpiryInMinutes, Map.of());
		} catch (final AuthException e) {
			return formClientError(e);
		}
	}
	
	public AppUserTO validateClientCredentials(final String clientId, final String secretKey) {
		if (null == clientId || null == secretKey) {
			throw new AuthException(AuthErrorCode.CLIENT_LOGIN_FAILURE);
		}
		final var user = userDao.findByClientId(clientId)
				.orElseThrow(() -> new AuthException(AuthErrorCode.CLIENT_LOGIN_FAILURE));
		validateCredentials(secretKey, user.getClientSecretHash());
		return user;
	}

	private ClientLoginResponse populateCustomTokenInfo(final AppUserTO userTo, final AuthUserType userType,
			final TokenType tokenType, final int expiryInMinutes, final Map<String, String> customFields) {
		final var metadata = userMetadataService.findUserMetadataMap(userTo.getUserId());
		metadata.putAll(customFields);
		final var expiry = metadata.containsKey(Constants.TOKEN_EXPIRY_IN_MINUTES)
				? Integer.parseInt(metadata.get(Constants.TOKEN_EXPIRY_IN_MINUTES))
				: expiryInMinutes;
		authenticationService.generateAccessToken(userTo, tokenType, userType, metadata,
				privilegeService.getUserPrivileges(userTo.getUserId()), expiry);
		return new ClientLoginResponse(userTo.getAccessToken(), userTo.getTokenExpiryTimestamp());
	}

	private ClientLoginResponse formClientError(final AuthException e) {
		return new ClientLoginResponse(e.getErrorCode().getMessage(), e.getErrorCode().getCode());
	}

	@Override
	public ClientLoginResponse userLogin(final ClientTO clientTO) {
		return userLogin(clientTO, TokenType.CLIENT, tokenExpiryInMinutes);
	}

	private ClientLoginResponse userLogin(final ClientTO clientLogin, final TokenType tokenType,
			final int expiryInMinutes) {
		try {
			log.info("User Token Creation {}", clientLogin);
			validateUserLogin(clientLogin);
			final var user = userDao.getUsersByUserName(clientLogin.getUserName())
					.orElseThrow(() -> new AuthException(AuthErrorCode.CLIENT_LOGIN_FAILURE));
			validateCredentials(clientLogin.getPassword(), user.getUserSecretHash());
			return populateCustomTokenInfo(user, clientLogin.getUserType(), tokenType, expiryInMinutes,
					createClientMetadata(user));
		} catch (final AuthException e) {
			return formClientError(e);
		}
	}

	private void validateUserLogin(final ClientTO clientLogin) {
		if (null == clientLogin.getUserName() || null == clientLogin.getPassword()) {
			throw new AuthException(AuthErrorCode.CLIENT_LOGIN_FAILURE);
		}
	}

	@Override
	public ClientLoginResponse userNeverExpireTokenLogin(final ClientTO clientTO) {
		return userLogin(clientTO, TokenType.CUSTOM, customTokenExpiryInDays);
	}

	private Map<String, String> createClientMetadata(final AppUserTO user) {
		final Map<String, String> clientMetadata = new HashMap<>();
		clientMetadata.put("userEmail", user.getUseremail());
		clientMetadata.put("userName", user.getUserName());
		return clientMetadata;
	}

	private void validateCredentials(final String password, final String secretHash) {
		if (!userService.matchPassword(password, secretHash)) {
			throw new AuthException(AuthErrorCode.CLIENT_LOGIN_FAILURE);
		}
	}
}
