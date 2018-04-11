package com.ireslab.coinclaim.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.coinclaim.entity.ClientAccount;

/**
 * @author iRESlab
 *
 */
public interface ClientAccountRepo extends CrudRepository<ClientAccount, Integer> {

	/**
	 * @param companyCorrelationId
	 * @return
	 */
	public ClientAccount findByClientCorrelationId(String clientCorrelationId);

}
