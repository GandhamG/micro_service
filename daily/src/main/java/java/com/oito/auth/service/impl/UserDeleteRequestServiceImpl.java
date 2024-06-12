/**
 *
 */
package com.oito.auth.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.BulkUserDeleteInputRequest;
import com.oito.auth.common.to.UserBulkDeleteResponse;
import com.oito.auth.common.to.UserDeleteInputRequest;
import com.oito.auth.common.to.UserDeleteRequestDTO;
import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.common.to.UserDeleteResponse;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.dao.UserDeleteRequestDAO;
import com.oito.auth.exception.AuthException;
import com.oito.auth.exception.errorcode.AuthErrorCode;
import com.oito.auth.jwt.TokenHandler;
import com.oito.auth.mapper.AppUserToUserDeleteResponseMapper;
import com.oito.auth.mapper.UserDeleteRequestDTOMapper;
import com.oito.auth.service.UserDeleteRequestService;
import com.oito.auth.service.UserService;
import com.oito.common.auth.jwt.JWETokenHandler;
import com.oito.common.util.Booleans;

/**
 * Auth0 specific implementation of UserService
 *
 * @author Dileep
 *
 */
@Service
public class UserDeleteRequestServiceImpl implements UserDeleteRequestService {
	@Autowired
	private UserDeleteRequestDAO userDeleteRequestDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private UserContextService userContextService;

	@Autowired
	private JWETokenHandler jweTokenHandler;

	@Autowired
	private TokenHandler tokenHandler;

	@Autowired
	private AppUserToUserDeleteResponseMapper appUserToUserDeleteResponseMapper;

	@Autowired
	private UserDeleteRequestDTOMapper userDeleteRequestDTOMapper;

	private static final int DB_UPDATE_BATCH_SIZE = 50;

	@Override
	@Transactional
	public UserDeleteResponse create(final UserDeleteInputRequest request) {
		final var userId = request.getUserId();
		final var dbUser = userService.getUserById(userId)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
		Booleans.throwIfTrue(dbUser.getStatus() == UserStatus.DELETED,
				() -> new AuthException(AuthErrorCode.DELETED_USER));
		validateUserDeleteRequest(request);
		final var user = updateUserStatus(userId, UserStatus.DELETE_REQUESTED);
		saveOrUpdate(request);
		final var response = formResponse(user);
		response.setReason(request.getReason());
		return response;
	}

	@Override
	@Transactional
	public UserDeleteResponse bulkCreate(final BulkUserDeleteInputRequest request) {
		UserDeleteResponse response = null;

		for (final Long userId : request.getUserIds()) {
			validateUserDeleteRequest(request);
			userService.getUserById(userId).orElseThrow(() -> new AuthException(AuthErrorCode.APP_USER_ID_EMPTY));
			final var user = updateUserStatus(userId, UserStatus.DELETE_REQUESTED);
			final var userDeleteInputRequest = new UserDeleteInputRequest();
			userDeleteInputRequest.setUserId(userId);
			saveOrUpdate(userDeleteInputRequest);
			response = formResponse(user);
		}
		response.setStatus(UserDeleteRequestStatus.OPEN);
		return response;
	}

	private UserDeleteResponse formAnonymousResponse(final AppUserTO user, final AppUserTO oldRecord) {
		final var userDeleteResponse = appUserToUserDeleteResponseMapper.toEntity(user);
		userDeleteResponse.setStatus(UserDeleteRequestStatus.OPEN);
		userDeleteResponse.setFullName(oldRecord.getFullName());
		userDeleteResponse.setUseremail(oldRecord.getUseremail());
		userDeleteResponse.setAnonymousEmail(user.getUseremail());
		userDeleteResponse.setPhoneNumber(oldRecord.getPhoneNo());
		userDeleteResponse.setUserId(user.getUserId());
		userDeleteResponse.setLang(user.getPreferredLanguage());
		return userDeleteResponse;
	}

	private UserBulkDeleteResponse formUserBulkDeleteResponse(final List<UserDeleteResponse> responseList) {
		final var userBulkDeleteResponse = new UserBulkDeleteResponse();
		userBulkDeleteResponse.setResponseList(responseList);
		return userBulkDeleteResponse;
	}

	private UserDeleteResponse formResponse(final AppUserTO user) {
		final var userDeleteResponse = appUserToUserDeleteResponseMapper.toEntity(user);
		userDeleteResponse.setStatus(UserDeleteRequestStatus.OPEN);
		userDeleteResponse.setFullName(user.getFullName());
		userDeleteResponse.setUseremail(user.getUseremail());
		userDeleteResponse.setPhoneNumber(user.getPhoneNo());
		userDeleteResponse.setUserId(user.getUserId());
		userDeleteResponse.setLang(user.getPreferredLanguage());
		return userDeleteResponse;
	}

	private AppUserTO updateUserStatus(final Long userId, final UserStatus status) {
		final var appUser = new AppUserTO();
		appUser.setUserId(userId);
		appUser.setStatus(status);
		return userService.updateUser(appUser);
	}

	@Override
	@Transactional
	public UserDeleteResponse update(final UserDeleteInputRequest request) {
		final var userDeleteRequestDTO = userDeleteRequestDAO
				.findByUserIdAndStatus(request.getUserId(), UserDeleteRequestStatus.OPEN)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_DELETE_REQUEST_NO_OPEN));

		final var userDTO = userService.getUserById(request.getUserId())
				.orElseThrow(() -> new AuthException(AuthErrorCode.APP_USER_ID_EMPTY));
		final var user = saveUserAnonymised(userDTO);
		userDeleteRequestDTO.setStatus(request.getStatus());
		userDeleteRequestDTO.setReason(request.getReason());
		userDeleteRequestDTO.setProcessedTimestamp(LocalDateTime.now());
		userDeleteRequestDAO.save(userDeleteRequestDTO);
		return formAnonymousResponse(user, userDTO);
	}

	@Override
	@Transactional
	public UserBulkDeleteResponse bulkDelete(final List<Long> userIds) {
		final List<UserDeleteResponse> responseList = new ArrayList<>();
		final var userMap = userService.getUsersByUserIds(userIds);
		final var userDeleteMap = userDeleteRequestDAO.findByUserIds(userIds);
		final List<Long> deletedUserIds = new ArrayList<>();
		final List<Long> undoUserIds = new ArrayList<>();

		for (final var entry : userDeleteMap.entrySet()) {
			final var userId = entry.getKey();
			final var userDeleteResponse = new UserDeleteResponse();
			userDeleteResponse.setUserId(userId);
			final var userDeleteRequestDTO = entry.getValue();
			final var appUserTO = userMap.get(userId);
			userDeleteResponse.setLang(appUserTO.getPreferredLanguage());
			userDeleteResponse.setUseremail(appUserTO.getUseremail());
			userDeleteResponse.setFullName(appUserTO.getFullName());
			if (userDeleteRequestDTO != null && userDeleteRequestDTO.getStatus().equals(UserDeleteRequestStatus.OPEN)) {
				deletedUserIds.add(userId);
				userDeleteResponse.setStatus(UserDeleteRequestStatus.COMPLETED);
				var email = appUserTO.getUseremail();
				email = email.replace(email.substring(0, email.indexOf('@')), appUserTO.getUserId() + "");
				userDeleteResponse.setAnonymousEmail(email);
				userDeleteResponse.setReason(userDeleteRequestDTO.getReason());
				responseList.add(userDeleteResponse);
			}

		}
		if (!deletedUserIds.isEmpty()) {
			final var batches = Lists.partition(deletedUserIds, DB_UPDATE_BATCH_SIZE);
			for (final List<Long> userIDs : batches) {
				userDeleteRequestDAO.updateUserDeleteRequest(userIDs, UserDeleteRequestStatus.COMPLETED);
				userService.bulkUpdateForUserDelete(userIDs, UserStatus.DELETED);
			}
		}
		if (!undoUserIds.isEmpty()) {
			final var batches = Lists.partition(undoUserIds, DB_UPDATE_BATCH_SIZE);
			for (final List<Long> userIDs : batches) {
				userDeleteRequestDAO.updateUserDeleteRequest(userIDs, UserDeleteRequestStatus.CANCELLED);
				userService.bulkUpdateForUserUnDelete(userIDs, UserStatus.ACTIVE);
			}

		}
		return formUserBulkDeleteResponse(responseList);
	}

	private UserDeleteResponse handleCancelled(final AppUserTO userDTO,
			final UserDeleteRequestDTO userDeleteRequestDTO) {

		final var appUser = new AppUserTO();
		appUser.setUserId(userDTO.getUserId());
		appUser.setStatus(UserStatus.ACTIVE);// Update User
		userService.updateUser(appUser);
		userDeleteRequestDTO.setStatus(UserDeleteRequestStatus.CANCELLED);
		userDeleteRequestDAO.save(userDeleteRequestDTO);
		return formResponse(userDTO);
	}

	public void validateTokenForUser(final Long userId, final String token) {
		if (!userId.toString().equals(
				jweTokenHandler.decodeToken(tokenHandler.getAccessTokenFromAuthorization(token)).get("userId"))) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
	}

	private void validateUserDeleteRequest(final UserDeleteInputRequest request) {
		if (request.getUserId() == null) {
			throw new AuthException(AuthErrorCode.APP_USER_ID_EMPTY);
		}
		if (request.getIgnoreLoggedIn() != null && !request.getIgnoreLoggedIn()) {
			userContextService.verifyAdminOrLoggedInUser(request.getUserId());
		}
		validateOpenRequest(request);
	}

	private void validateUserDeleteRequest(final BulkUserDeleteInputRequest request) {

		for (final Long userId : request.getUserIds()) {
			if (userId == null) {
				throw new AuthException(AuthErrorCode.APP_USER_ID_EMPTY);
			}
			final var count = userDeleteRequestDAO.countByUserIdAndStatus(userId, UserDeleteRequestStatus.OPEN);
			if (count.longValue() > 0) {
				throw new AuthException(AuthErrorCode.USER_DELETE_REQUEST_OPEN);
			}
		}

	}

	private void validateOpenRequest(final UserDeleteInputRequest request) {
		final var count = userDeleteRequestDAO.countByUserIdAndStatus(request.getUserId(),
				UserDeleteRequestStatus.OPEN);
		if (count.longValue() > 0) {
			throw new AuthException(AuthErrorCode.USER_DELETE_REQUEST_OPEN);
		}
	}

	private AppUserTO saveUserAnonymised(final AppUserTO user) {
		final var appUser = new AppUserTO();
		appUser.setUserId(user.getUserId());
		final var userIDString = user.getUserId().toString();
		var email = user.getUseremail();
		email = email.replace(email.substring(0, email.indexOf('@')), userIDString);
		appUser.setFullName(userIDString);
		appUser.setUseremail(email);
		appUser.setPhoneNo(userIDString);
		appUser.setStatus(UserStatus.DELETED);
		return userService.updateUser(appUser);
	}

	@Override
	@Transactional
	public UserDeleteResponse rollback(final UserDeleteResponse response) {

		final var userDeleteRequestDTO = userDeleteRequestDAO
				.findByUserIdAndStatus(response.getUserId(), UserDeleteRequestStatus.COMPLETED)
				.orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_APP_USER_DATA));
		userDeleteRequestDTO.setStatus(UserDeleteRequestStatus.OPEN);
		userDeleteRequestDAO.save(userDeleteRequestDTO);
		final var userto = userService.updateUser(getResetUserData(response));
		return formResponse(userto);
	}

	private AppUserTO getResetUserData(final UserDeleteResponse response) {
		final var usetTO = new AppUserTO();
		usetTO.setUseremail(response.getUseremail());
		usetTO.setFullName(response.getFullName());
		usetTO.setPhoneNo(response.getPhoneNumber());
		usetTO.setStatus(UserStatus.DELETE_REQUESTED);
		usetTO.setUserId(response.getUserId());
		return usetTO;
	}

	@Override
	public List<UserDeleteResponse> getUserDeleteRequestByStatus(final UserDeleteRequestStatus status,
			final Long interval) {

		return userDeleteRequestDAO.findByStatus(status, interval);
	}

	@Override
	public UserDeleteResponse undoDelete(final Long userId) {
		userContextService.verifyAdminOrLoggedInUser(userId);
		final var userDTO = userService.getUserById(userId)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
		final var userDeleteRequestDTO = userDeleteRequestDAO
				.findByUserIdAndStatus(userId, UserDeleteRequestStatus.OPEN)
				.orElseThrow(() -> new AuthException(AuthErrorCode.USER_DELETE_REQUEST_NO_OPEN));
		return handleCancelled(userDTO, userDeleteRequestDTO);
	}

	private void saveOrUpdate(final UserDeleteInputRequest request) {
		final var dto = userDeleteRequestDAO.findByUserId(request.getUserId());
		if (dto.isEmpty()) {
			request.setProcessedTimestamp(LocalDateTime.now());
			userDeleteRequestDAO.save(userDeleteRequestDTOMapper.toEntity(request));
		} else {
			final var userDeleteRequestDTO = dto.get();
			userDeleteRequestDTO.setStatus(request.getStatus());
			userDeleteRequestDTO.setProcessedTimestamp(LocalDateTime.now());
			userDeleteRequestDTO.setReason(request.getReason());
			userDeleteRequestDAO.update(userDeleteRequestDTO);
		}

	}

	@Override
	@Transactional
	public UserBulkDeleteResponse undoBulkDelete(final List<Long> userIds) {
		final List<UserDeleteResponse> responseList = new ArrayList<>();
		final var userDeleteMap = userDeleteRequestDAO.findByUserIds(userIds);
		final var userMap = userService.getUsersByUserIds(userIds);
		final List<Long> deletedUserIds = new ArrayList<>();

		for (final var entry : userDeleteMap.entrySet()) {
			final var userId = entry.getKey();
			final var userDeleteRequestDTO = entry.getValue();
			if (userDeleteRequestDTO != null && userDeleteRequestDTO.getStatus().equals(UserDeleteRequestStatus.OPEN)) {
				final var user = userMap.get(userId);
				if (user == null) {
					continue;
				}
				final var userDeleteResponse = new UserDeleteResponse();
				userDeleteResponse.setUserId(userId);
				userDeleteResponse.setUseremail(user.getUseremail());
				userDeleteResponse.setFullName(user.getFullName());
				userDeleteResponse.setLang(user.getPreferredLanguage());
				responseList.add(userDeleteResponse);
				deletedUserIds.add(userId);
			}

		}
		if (!deletedUserIds.isEmpty()) {
			final var batches = Lists.partition(deletedUserIds, DB_UPDATE_BATCH_SIZE);
			for (final List<Long> userIDs : batches) {
				userDeleteRequestDAO.updateUserDeleteRequest(userIDs, UserDeleteRequestStatus.CANCELLED);
				userService.bulkUpdateForUserUnDelete(userIDs, UserStatus.ACTIVE);
			}

		}
		return formUserBulkDeleteResponse(responseList);
	}

}