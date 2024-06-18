/**
 *
 */
package com.oito.auth.service;

import java.util.List;

import com.oito.auth.common.to.BulkUserDeleteInputRequest;
import com.oito.auth.common.to.UserBulkDeleteResponse;
import com.oito.auth.common.to.UserDeleteInputRequest;
import com.oito.auth.common.to.UserDeleteRequestStatus;
import com.oito.auth.common.to.UserDeleteResponse;

/**
 * Basic User Services interface
 *
 * @author Dileep
 *
 */

public interface UserDeleteRequestService {

	UserDeleteResponse create(UserDeleteInputRequest request);

	UserDeleteResponse update(UserDeleteInputRequest request);

	UserDeleteResponse undoDelete(Long userId);

	UserBulkDeleteResponse bulkDelete(final List<Long> userIds);

	UserBulkDeleteResponse undoBulkDelete(final List<Long> userIds);

	List<UserDeleteResponse> getUserDeleteRequestByStatus(UserDeleteRequestStatus status, Long interval);

	UserDeleteResponse rollback(UserDeleteResponse response);

	UserDeleteResponse bulkCreate(BulkUserDeleteInputRequest request);

}
