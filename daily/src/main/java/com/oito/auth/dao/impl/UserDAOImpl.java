package com.oito.auth.dao.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.oito.auth.common.AuthProvider;
import com.oito.auth.common.AuthUserType;
import com.oito.auth.common.Constants;
import com.oito.auth.common.enumeration.CommunicationChannel;
import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.ChangeEmailRequest;
import com.oito.auth.common.to.ListResponse;
import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.UserIdentifierSearchRequest;
import com.oito.auth.common.to.UserListRequest;
import com.oito.auth.common.to.UserStatus;
import com.oito.auth.common.to.UserTypeTO;
import com.oito.auth.dao.UserDAO;
import com.oito.auth.dao.repository.EmailUpdateHistoryRepository;
import com.oito.auth.dao.repository.UserRepository;
import com.oito.auth.dao.repository.UserTypeRepository;
import com.oito.auth.data.EmailUpdateHistory;
import com.oito.auth.data.User;
import com.oito.auth.data.UserType;
import com.oito.auth.mapper.AppUserToUserMapper;
import com.oito.auth.mapper.UserTypeMapper;
import com.oito.auth.util.UserUtils;
import com.oito.common.usercontext.UserContextStore;

import io.micrometer.core.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserDAOImpl implements UserDAO {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserTypeMapper userTypeMapper;

	@Autowired
	private AppUserToUserMapper userMapper;
	@Autowired
	private UserTypeRepository userTypeRepository;

	@Autowired
	private EmailUpdateHistoryRepository emailHistoryRepo;

	@Autowired
	private UserContextStore userContextStore;

	@Override
	public List<AppUserTO> filterUserByMetadata(final String code, final String value) {
		return toVOList(userRepository.filterUserByMetadata(code, value));
	}

	@Override
	public List<AppUserTO> getUsersByEmails(final List<String> emailList) {
		return toVOList(userRepository.findByUseremailIn(emailList));
	}

	@Override
	public List<AppUserTO> getUsersByPhoneNos(final List<String> phoneNos) {
		return toVOList(userRepository.findByPhoneNoIn(phoneNos));
	}

	@Override
	public Optional<AppUserTO> getUserByPhone(final String phone) {
		return Optional.ofNullable(phone).filter(StringUtils::isNotBlank).flatMap(userRepository::findByPhoneNo)
				.map(this::toAppUserTO);
	}

	@Override
	public Optional<AppUserTO> findById(final Long id) {
		return userRepository.findById(id).map(this::toAppUserTO);
	}

	@Override
	public Map<Long, AppUserTO> getUsersByUserIds(final List<Long> userIds) {
		return toVOList(userRepository.findByUserIdIn(userIds)).stream()
				.collect(Collectors.toMap(AppUserTO::getUserId, u -> u));
	}

	@Override
	public Optional<AppUserTO> getUsersByUserEmail(final String userEmail) {
		return Optional.ofNullable(userEmail).filter(StringUtils::isNotBlank).flatMap(userRepository::findByUseremail)
				.map(this::toAppUserTO);
	}

	@Override
	public Optional<AppUserTO> findByUserEmailAndType(final String userEmail, @NonNull final AuthUserType type) {
		return Optional.ofNullable(userEmail).filter(StringUtils::isNotBlank)
				.flatMap(email -> userRepository.findByUseremailAndUserType(email, type)).map(this::toAppUserTO);
	}

	@Override
	public Optional<AppUserTO> getUsersByUserName(final String userName) {
		return userRepository.findByUserName(userName).map(this::toAppUserTO);
	}

	@Override
	public List<AppUserTO> getUsersByUserNameOrEmailOrPhone(final String userName, final String userEmail,
			final String phone) {
		return toVOList(userRepository.findByUserNameOrUseremailOrPhoneNo(userName, userEmail, phone));
	}

	@Override
	public void save(final User user) {
		userRepository.save(user);
	}

	@Override
	public Long countByUseremail(final String userEmail) {
		return userRepository.countByUseremail(userEmail);
	}

	@Override
	public Optional<AppUserTO> findBySocialId(final String socialId, final AuthProvider provider) {
		var user = Optional.<User>empty();
		if (AuthProvider.GOOGLE.equals(provider)) {
			user = userRepository.findByGoogleId(socialId);
		} else if (AuthProvider.FACEBOOK.equals(provider)) {
			user = userRepository.findByFacebookId(socialId);
		} else if (AuthProvider.LINE.equals(provider)) {
			user = userRepository.findByLineId(socialId);
		}
		return user.map(this::toAppUserTO);
	}

	@Override
	public void updateSocialInfo(final AppUserTO userTO, final SocialLoginRequest loginRequest) {
		log.info("{} Social Login Request {}", loginRequest.getAuthProvider(), loginRequest);
		if (AuthProvider.GOOGLE.equals(loginRequest.getAuthProvider()) && userTO.getGoogleId() == null) {
			userRepository.updateGoogleSocial(userTO.getUserId(), loginRequest.getSocialId());
		} else if (AuthProvider.FACEBOOK.equals(loginRequest.getAuthProvider()) && userTO.getFacebookId() == null) {
			userRepository.updateFacebookSocial(userTO.getUserId(), loginRequest.getSocialId());
		} else if (AuthProvider.LINE.equals(loginRequest.getAuthProvider()) && userTO.getLineId() == null) {
			userRepository.updateLineSocial(userTO.getUserId(), loginRequest.getSocialId());
		}
	}

	@Override
	public UserTypeTO updateUserType(final UserType userType) {
		userTypeRepository.save(userType);
		return userTypeMapper.toVO(userType);
	}

	@Override
	public Optional<AppUserTO> findByClientId(final String clientId) {
		return userRepository.findByClientId(clientId).map(this::toAppUserTO);
	}

	@Override
	public ListResponse<AppUserTO> searchUser(final UserListRequest userListRequest) {
		final var responseList = userRepository.findAll(buildSearchQuery(userListRequest),
				populatePageRequest(userListRequest));
		return forListResponse(userListRequest, responseList);
	}

	@Override
	public Long searchUserCount(final UserListRequest userListRequest) {
		return Long.valueOf(userRepository.count(buildSearchQuery(userListRequest)));
	}

	private ListResponse<AppUserTO> forListResponse(final UserListRequest listRequest, final Page<User> requestData) {
		return ListResponse.<AppUserTO>builder().itemsPerPage(listRequest.getItemsPerPage()).page(listRequest.getPage())
				.totalItemsCount(requestData.getTotalElements()).rowData(toVOList(requestData.getContent())).build();

	}

	private List<AppUserTO> toVOList(final List<User> typeList) {
		return userMapper.toVOList(typeList);
	}

	private AppUserTO toAppUserTO(final User user) {
		return userMapper.toVO(user);
	}

	private PageRequest populatePageRequest(final UserListRequest listRequest) {
		return PageRequest.of(listRequest.getPage() - 1, listRequest.getItemsPerPage(),
				JpaSort.unsafe(Direction.DESC, listRequest.getOrderBy()));
	}

	@Override
	public void changeEmailHistory(final ChangeEmailRequest changeEmailRequest) {
		final var history = new EmailUpdateHistory();
		history.setFromEmail(changeEmailRequest.getUseremail());
		history.setToEmail(changeEmailRequest.getNewUseremail());
		history.setCreatedTimestamp(LocalDateTime.now());
		history.setCreatedBy(userContextStore.getUserContext().getUserId());
		emailHistoryRepo.save(history);
	}

	@Override
	public List<AppUserTO> findAll() {
		return toVOList(userRepository.findAll());
	}

	@Override
	public int updateLastAccessToken(final Long userId, final String currentToken, final String newToken) {
		return userRepository.updateLastAccessToken(userId, currentToken, newToken);
	}

	@Override
	public void bulkUpdateForUserDelete(final List<Long> userIds, final UserStatus status) {
		userRepository.bulkUpdateForUserDelete(userIds, status);
	}

	@Override
	public Long countByPhoneNo(final String phoneNo) {
		return userRepository.countByPhoneNo(phoneNo);
	}

	@Override
	public Long countByPhoneNoAndUserType(final String phoneNo, final AuthUserType userType) {
		return userRepository.countByPhoneNoAndUserType(phoneNo, userType);
	}

	private Specification<User> buildSearchQuery(final UserListRequest userListRequest) {
		return (root, query, criteriaBuilder) -> {

			final List<Predicate> predicates = new ArrayList<>();
			addDefaultSearchConditions(root, criteriaBuilder, predicates, userListRequest);
			if (userListRequest.getSearchText() != null) {
				addSearchPredicate(userListRequest, root, criteriaBuilder, predicates);
			}
			if (userListRequest.getUserStatus() != null) {
				predicates.add(userListRequest.getUserStatus().getPredicate(root, criteriaBuilder));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		};
	}

	private void addDefaultSearchConditions(final Root<User> root, final CriteriaBuilder criteriaBuilder,
			final List<Predicate> predicates, final UserListRequest userListRequest) {
		final var userTypeJoin = root.join(Constants.USER_TYPE_FIELD);
		// 1. User type
		predicates.add(criteriaBuilder.and(
				criteriaBuilder.equal(userTypeJoin.get(Constants.TYPE_FIELD), userListRequest.getSearchUserType())));
		// 2. status != DELETED
		final var skipDeletedPredicate = criteriaBuilder
				.and(criteriaBuilder.notEqual(root.get(Constants.STATUS_FIELD), UserStatus.DELETED));
		final var includeNullPredicate = criteriaBuilder.and(criteriaBuilder.isNull(root.get(Constants.STATUS_FIELD)));

		predicates.add(criteriaBuilder.and(criteriaBuilder.or(skipDeletedPredicate, includeNullPredicate)));
	}

	private void addSearchPredicate(final UserListRequest userListRequest, final Root<User> root,
			final CriteriaBuilder criteriaBuilder, final List<Predicate> predicates) {
		final var searchText = buildLikeValue(userListRequest.getSearchText());
		if (userListRequest.getSearchField() == null) {
			predicates.add(criteriaBuilder
					.and(criteriaBuilder.or(buildLikeClause(root, criteriaBuilder, Constants.PHONE_FIELD, searchText),
							buildLikeClause(root, criteriaBuilder, Constants.EMAIL_FIELD, searchText))));
		} else {
			predicates.add(criteriaBuilder
					.and(buildLikeClause(root, criteriaBuilder, userListRequest.getSearchField(), searchText)));
		}
	}

	private Predicate buildLikeClause(final Root<User> root, final CriteriaBuilder criteriaBuilder, final String field,
			final String searchText) {
		return criteriaBuilder.like(root.get(field), searchText);
	}

	private String buildLikeValue(final String value) {
		return String.format(Constants.LIKE_FORMAT, value);
	}

	@Override
	public List<AppUserTO> getByUserIdentifierList(final UserIdentifierSearchRequest request) {
		return toVOList(userRepository.findByUserIdInOrUseremailInOrPhoneNoInOrLineIdIn(request.getUserIdList(),
				request.getUserEmailList(), request.getPhoneNoList(), request.getLineIdList()));
	}

	@Override
	public void updateLineSocial(final Long userId, final String lineId) {
		userRepository.updateLineSocial(userId, lineId);
	}

	@Override
	public void updateCommunicationChannel(final Long userId, final Set<CommunicationChannel> communicationChannels) {
		userRepository.updateCommunicationChannelInfo(userId,
				UserUtils.joinCommunicationChannel(communicationChannels));
	}

	@Override
	public List<AppUserTO> findUserByPrivilege(final String privilegeCode, final String accessCode) {
		return toVOList(userRepository.findUserByPrivilege(privilegeCode, accessCode));
	}

}