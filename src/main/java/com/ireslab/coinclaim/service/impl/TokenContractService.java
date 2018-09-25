package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import com.ireslab.coinclaim.exception.ApiException;
import com.ireslab.coinclaim.model.Error;
import com.ireslab.coinclaim.utils.AppConstants;
import com.ireslab.coinclaim.utils.ResponseCode;
import com.ireslab.coinclaim.utils.TokenConfig;

/**
 * @author iRESlab
 *
 */
public class TokenContractService extends Contract {

	private static final Logger LOG = LoggerFactory.getLogger(TokenContractService.class);

	private static final String TOKEN_ALLOCATION_CONTRACT_METHOD = "transfer";
	private static final String BALANCE_CHECK_METHOD = "balanceOf";

	/**
	 * @param contractBinary
	 * @param contractAddress
	 * @param web3j
	 * @param credentials
	 * @param gasPrice
	 * @param gasLimit
	 */
	protected TokenContractService(String contractBinary, String contractAddress, Web3j web3j,
			Credentials credentials) {
		super(contractBinary, contractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
	}

	/**
	 * @param contractBinary
	 * @param contractAddress
	 * @param web3j
	 * @param credentials
	 * @param gasPrice
	 * @param gasLimit
	 */
	protected TokenContractService(String contractBinary, String contractAddress, Web3j web3j, Credentials credentials,
			BigInteger gasPrice, BigInteger gasLimit) {
		super(contractBinary, contractAddress, web3j, credentials, gasPrice, gasLimit);
	}

	/**
	 * @param web3j
	 * @param tokenConfig
	 * @return
	 */
	public static TokenContractService getContractServiceInstance(Web3j web3j, TokenConfig tokenConfig) {

		TokenContractService ccTokenContractService;
		String tokenContractAddress = new String(tokenConfig.getTokenContractAddress()).intern();

		synchronized (tokenContractAddress) {
			ccTokenContractService = new TokenContractService(tokenConfig.getTokenContractBinary(),
					tokenConfig.getTokenContractAddress(), web3j,
					Credentials.create(tokenConfig.getTokenDeployerPrivateKey()));
		}

		return ccTokenContractService;
	}

	/**
	 * @param web3j
	 * @param tokenConfig
	 * @param gasPrice
	 * @param gasLimit
	 * @return
	 */
	public static TokenContractService getContractServiceInstance(Web3j web3j, TokenConfig tokenConfig,
			BigInteger gasPrice, BigInteger gasLimit) {

		TokenContractService ccTokenContractService;
		String tokenContractAddress = new String(tokenConfig.getTokenContractAddress()).intern();

		synchronized (tokenContractAddress) {
			ccTokenContractService = new TokenContractService(tokenConfig.getTokenContractBinary(),
					tokenConfig.getTokenContractAddress(), web3j,
					Credentials.create(tokenConfig.getTokenDeployerPrivateKey()), gasPrice, gasLimit);
		}

		return ccTokenContractService;
	}

	/**
	 * @param beneficiaryAddress
	 * @param tokenQuantity
	 */
	@SuppressWarnings("rawtypes")
	public TransactionReceipt allocateTokens(String beneficiaryAddress, BigInteger tokenQuantity) throws Exception {

		LOG.debug("Request received for token allocation for beneficiaryAddress - " + beneficiaryAddress
				+ ", tokenQuantity - " + tokenQuantity);

		Function tokenAllocationFunction = new Function(TOKEN_ALLOCATION_CONTRACT_METHOD,
				Arrays.<Type>asList(new Address(beneficiaryAddress), new Uint256(tokenQuantity)),
				Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
				}));

		TransactionReceipt transactionReceipt = null;
		try {
			LOG.debug("Executing transaction - " + tokenAllocationFunction);
			transactionReceipt = executeTransaction(tokenAllocationFunction);
			LOG.debug("Transaction Receipt Status - " + transactionReceipt.getStatus());

		} catch (Exception exp) {
			LOG.error("Error occurred while executing token allocation transaction - "
					+ ExceptionUtils.getStackTrace(exp));

			if (exp.getMessage().indexOf(AppConstants.INTRINSIC_GAS_TOO_LOW) != -1) {
				throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(),
						HttpStatus.INTERNAL_SERVER_ERROR.name(), Arrays.asList(
								new Error(ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(), "Intrinsic gas too low")));

			} else if (exp.getMessage().indexOf(AppConstants.EXCEEDS_BLOCK_GAS_LIMIT) != -1) {
				throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.GAS_LIMIT_EXCEEDS.getCode(),
						HttpStatus.INTERNAL_SERVER_ERROR.name(),
						Arrays.asList(new Error(ResponseCode.GAS_LIMIT_EXCEEDS.getCode(), "Exceeds block gas limit")));
			}

			throw new Exception(exp);
		}

		return transactionReceipt;
	}

	/**
	 * @param beneficiaryAddress
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public BigInteger retrieveBalance(String beneficiaryAddress) {

		BigInteger balance = null;
		LOG.debug("Request received for retrieving balance for beneficiaryAddress - " + beneficiaryAddress);

		Function tokenAllocationFunction = new Function(BALANCE_CHECK_METHOD,
				Arrays.<Type>asList(new Address(beneficiaryAddress)),
				Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
				}));

		try {
			balance = (BigInteger) executeCallSingleValueReturn(tokenAllocationFunction).getValue();
			LOG.debug("Account Balance - " + balance);
			return balance;

		} catch (Exception exp) {
			LOG.error("Error occurred while executing token allocation transaction - "
					+ ExceptionUtils.getStackTrace(exp));
		}

		return null;
	}
}
