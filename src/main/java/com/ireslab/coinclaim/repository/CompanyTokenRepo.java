package com.ireslab.coinclaim.repository;

import org.springframework.data.repository.CrudRepository;

import com.ireslab.coinclaim.entity.CompanyToken;

/**
 * @author iRESlab
 *
 */
public interface CompanyTokenRepo extends CrudRepository<CompanyToken, Integer> {

	/**
	 * @param tokenSymbol
	 * @param companyAccountId
	 * @return
	 */
	public CompanyToken findByTokenSymbolAndCompanyAccount_CompanyAccountId(String tokenSymbol,
			Integer companyAccountId);
	
	/**
	 * @param tokenSymbol
	 * @return
	 */
	public CompanyToken findByTokenSymbol(String tokenSymbol);
}
