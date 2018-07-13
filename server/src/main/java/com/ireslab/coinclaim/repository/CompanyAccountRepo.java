package com.ireslab.coinclaim.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.coinclaim.entity.CompanyAccount;

/**
 * @author iRESlab
 *
 */
public interface CompanyAccountRepo extends CrudRepository<CompanyAccount, Integer> {

	/**
	 * @param companyCorrelationId
	 * @return
	 */
	public CompanyAccount findByCompanyCorrelationId(String companyCorrelationId);

}
