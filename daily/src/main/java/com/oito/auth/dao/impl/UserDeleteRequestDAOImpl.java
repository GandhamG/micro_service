package com.oito.auth.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oito.auth.common.to.UserDeleteRequestDTO;
import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.common.to.UserDeleteResponse;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.dao.UserDeleteRequestDAO;
import com.oito.auth.dao.repository.UserDeleteRequestRepository;
import com.oito.auth.mapper.UserDeleteRequestMapper;

@Component
public class UserDeleteRequestDAOImpl implements UserDeleteRequestDAO {

	@Autowired
	private UserDeleteRequestRepository userDeleteRequestRepository;

	@Autowired
	private UserDeleteRequestMapper mapper;

	@Autowired
	private UserDAO userDAO;

	@Override
	public void save(final UserDeleteRequestDTO userDeleteRequest) {
		userDeleteRequestRepository.save(mapper.toEntity(userDeleteRequest));
	}

	@Override
	public void update(final UserDeleteRequestDTO userDeleteRequest) {
		userDeleteRequestRepository.save(mapper.toEntity(userDeleteRequest));
	}

	@Override
	public Optional<UserDeleteRequestDTO> findByUserId(final Long userId) {
		return userDeleteRequestRepository.findByUserId(userId).map(mapper::toVO);
	}

	@Override
	public Optional<UserDeleteRequestDTO> findByUserIdAndStatus(final Long userId,
			final UserDeleteRequestStatus status) {
		return userDeleteRequestRepository.findByUserIdAndStatus(userId, status).map(mapper::toVO);
	}

	@Override
	public List<UserDeleteResponse> findByStatus(final UserDeleteRequestStatus status, final Long agingInterval) {
		final var users = userDeleteRequestRepository
				.findOpenDeleteRequestInterval(LocalDateTime.now().minusDays(agingInterval), status);
		List<UserDeleteResponse> responseList = null;

		if (users != null && !users.isEmpty()) {
			final var userMap = userDAO.getUsersByUserIds(users);
			responseList = new ArrayList<>();
			for (final Long userId : users) {
				final var response = new UserDeleteResponse();
				response.setUserId(userId);
				response.setUseremail(userMap.get(userId).getUseremail());
				response.setUserTypes(userMap.get(userId).getUserTypes());
				responseList.add(response);
			}
		}
		return responseList;
	}

	@Override
	public void updateUserDeleteRequest(final UserDeleteRequestDTO dto) {
		userDeleteRequestRepository.updateUserDeleteRequest(dto.getUserId(), dto.getStatus());
	}

	@Override
	public Long countByUserIdAndStatus(final Long userId, final UserDeleteRequestStatus status) {
		return userDeleteRequestRepository.countByUserIdAndStatus(userId, status);
	}

	@Override
	public Map<Long, UserDeleteRequestDTO> findByUserIds(final List<Long> userIds) {
		return mapper.toVOList(userDeleteRequestRepository.findByUserIdIn(userIds)).stream()
				.collect(Collectors.toMap(UserDeleteRequestDTO::getUserId, u -> u));
	}

	@Override
	public void updateUserDeleteRequest(final List<Long> userIds, final UserDeleteRequestStatus status) {
		userDeleteRequestRepository.updateUserDeleteRequest(userIds, status);

	}

}