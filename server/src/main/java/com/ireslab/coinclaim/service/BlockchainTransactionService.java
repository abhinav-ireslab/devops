package com.ireslab.coinclaim.service;

import java.math.BigInteger;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TransactionDto;

/**
 * @author iRESlab
 *
 */
public interface BlockchainTransactionService {

	/**
	 * @param index
	 * @return
	 */
	public AddressDto generateAddress(BigInteger index);

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

}
