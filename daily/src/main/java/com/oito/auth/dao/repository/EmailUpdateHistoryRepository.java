/**
 *
 */
package com.oito.auth.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oito.auth.data.EmailUpdateHistory;

/**
 * @author Jobin John
 *
 */
public interface EmailUpdateHistoryRepository extends JpaRepository<EmailUpdateHistory, Long> {

}
