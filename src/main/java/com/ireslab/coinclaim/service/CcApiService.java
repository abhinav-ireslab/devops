package com.ireslab.coinclaim.service;

import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationRequest;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationResponse;
import com.ireslab.coinclaim.model.TokenTransferRequest;
import com.ireslab.coinclaim.model.TokenTransferResponse;

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
	public TokenTransferResponse transferTokens(TokenTransferRequest transferTokensRequest);

	/**
	 * @param tokenDetailsRegistrationRequest
	 * @return
	 */
	public TokenDetailsRegistrationResponse saveTokenDetails(
			TokenDetailsRegistrationRequest tokenDetailsRegistrationRequest);
	

	/**
	 * @param accountBalanceRequest
	 * @return
	 */
	// public AccountBalanceResponse retrieveUserTokenBalance(AccountBalanceRequest
	// accountBalanceRequest);

}
