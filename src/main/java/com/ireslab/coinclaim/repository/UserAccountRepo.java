package com.ireslab.coinclaim.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.coinclaim.entity.UserAccount;

/**
 * @author iRESlab
 *
 */
public interface UserAccountRepo extends CrudRepository<UserAccount, Integer> {

	/**
	 * @param userCorrelationId
	 * @return
	 */
	public UserAccount findByUserCorrelationId(String userCorrelationId);

}
