package com.ireslab.coinclaim.service;

import java.math.BigInteger;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TokenDetailsDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.utils.ClientType;

/**
 * @author iRESlab
 *
 */
public interface BlockchainTransactionService {

	/**
	 * @param index
	 * @param clientType
	 * @return
	 */
	public AddressDto generateAddress(BigInteger index, ClientType clientType);

	/**
	 * @param address
	 * @return
	 */
	public TransactionDto retrieveBalance(String address);

	/**
	 * @param transactionDto
	 * @return
	 */
	public TransactionDto transferTokens(TransactionDto transactionDto);

	/**
	 * @param index
	 * @param clientType
	 * @return
	 */
	public AddressDto derivePrivateKey(BigInteger index, ClientType clientType);

}
