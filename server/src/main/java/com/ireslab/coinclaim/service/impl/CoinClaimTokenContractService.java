package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;
import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.ireslab.coinclaim.utils.CLMTokenConfig;

/**
 * @author iRESlab
 *
 */
public class CoinClaimTokenContractService extends Contract {

	private static final Logger LOG = LoggerFactory.getLogger(CoinClaimTokenContractService.class);

	private static CoinClaimTokenContractService ccTokenContractService = null;
	private static Object whileCreatingObject = new Object();

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
	protected CoinClaimTokenContractService(String contractBinary, String contractAddress, Web3j web3j,
			Credentials credentials) {
		super(contractBinary, contractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
	}

	/**
	 * @param web3j
	 * @param tokenConfig
	 * @return
	 */
	public static CoinClaimTokenContractService getContractServiceInstance(Web3j web3j, CLMTokenConfig tokenConfig) {

		if (ccTokenContractService == null) {

			synchronized (whileCreatingObject) {
				ccTokenContractService = new CoinClaimTokenContractService(tokenConfig.getTokenContractBinary(),
						tokenConfig.getTokenContractAddress(), web3j,
						Credentials.create(tokenConfig.getTokenDeployerPrivateKey()));
			}
		}

		return ccTokenContractService;
	}

	/**
	 * @param beneficiaryAddress
	 * @param tokenQuantity
	 */
	@SuppressWarnings("rawtypes")
	public TransactionReceipt allocateTokens(String beneficiaryAddress, BigInteger tokenQuantity) {

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
		}

		return transactionReceipt;
	}

	/**
	 * @param beneficiaryAddress
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public BigInteger retrieveBalance(String beneficiaryAddress) {

		LOG.debug("Request received for retrieving balance for beneficiaryAddress - " + beneficiaryAddress);

		Function tokenAllocationFunction = new Function(BALANCE_CHECK_METHOD,
				Arrays.<Type>asList(new Address(beneficiaryAddress)),
				Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
				}));

		try {
			LOG.debug("Executing transaction - " + tokenAllocationFunction);
			return ((BigInteger) executeCallSingleValueReturn(tokenAllocationFunction).getValue());

		} catch (Exception exp) {
			LOG.error("Error occurred while executing token allocation transaction - "
					+ ExceptionUtils.getStackTrace(exp));
		}

		return null;
	}
}
