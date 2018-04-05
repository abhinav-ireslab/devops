package com.ireslab.coinclaim.service;

import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TransferTokensRequest;
import com.ireslab.coinclaim.model.TransferTokensResponse;

/**
 * @author iRESlab
 *
 */
public interface CcApiService {

	/**
	 * @param generateAddressRequest
	 * @return
	 */
	public GenerateAddressResponse generateAddress(GenerateAddressRequest generateAddressRequest);

	/**
	 * @param accountBalanceRequest
	 * @return
	 */
	public AccountBalanceResponse retrieveBalance(AccountBalanceRequest accountBalanceRequest);

	/**
	 * @param transferTokensRequest
	 * @return
	 */
	public TransferTokensResponse transferTokens(TransferTokensRequest transferTokensRequest);
}
