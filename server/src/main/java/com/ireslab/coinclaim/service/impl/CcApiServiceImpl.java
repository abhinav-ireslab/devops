package com.ireslab.coinclaim.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.entity.ClientAccount;
import com.ireslab.coinclaim.exception.ApiException;
import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.AccountDetails;
import com.ireslab.coinclaim.model.BaseApiRequest;
import com.ireslab.coinclaim.model.Error;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TransferTokensRequest;
import com.ireslab.coinclaim.model.TransferTokensResponse;
import com.ireslab.coinclaim.repository.ClientAccountRepo;
import com.ireslab.coinclaim.service.BlockchainTransactionService;
import com.ireslab.coinclaim.service.CcApiService;
import com.ireslab.coinclaim.service.CommonService;
import com.ireslab.coinclaim.utils.AppConstants;
import com.ireslab.coinclaim.utils.RequestType;
import com.ireslab.coinclaim.utils.ResponseCode;

/**
 * @author iRESlab
 *
 */
@Service
public class CcApiServiceImpl implements CcApiService {

	private static final Logger LOG = LoggerFactory.getLogger(CcApiService.class);

	@Autowired
	private CommonService commonService;

	@Autowired
	private ClientAccountRepo clientAccountRepo;

	@Autowired
	@Qualifier("ethereumTransactionServiceImpl")
	private BlockchainTransactionService ethereumTxnService;

	@Autowired
	@Qualifier("bitcoinTransactionServiceImpl")
	private BlockchainTransactionService bitcoinTxnService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#generateAddress(com.ireslab.
	 * coinclaim.model.GenerateAddressRequest)
	 */
	@Override
	public GenerateAddressResponse generateAddress(GenerateAddressRequest generateAddressRequest) {

		GenerateAddressResponse generateAddressResponse = null;

		// Validating Api Request
		validateBaseApiRequest(generateAddressRequest);

		String clientCorrelationId = generateAddressRequest.getClientCorrelationId();

		ClientAccount clientAccount = clientAccountRepo.findByClientCorrelationId(clientCorrelationId);

		if (clientAccount != null && clientAccount.getBtcAddress() != null) {
			LOG.error("Invalid correlation id | Client already exists with client correlation id - "
					+ clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.CLIENT_ALREADY_EXISTS.getCode(),
							"Client already exists with Correlation Id")));
		}

		// Create unique index
		BigInteger childIndex = commonService.getUniqueAddressIndex();
		LOG.debug("Unique child index generated - " + childIndex);

		// Call node service
		AddressDto addressDto = null;

		try {
			addressDto = bitcoinTxnService.generateAddress(childIndex);
			LOG.debug("Unique generated addresses:\n" + "\t Bitcoin (BTC) Address generated - "
					+ addressDto.getUniqueBitcoinAddress() + "\n\t Unique Ethereum (ETH) Address generated - "
					+ addressDto.getUniqueEthereumAddress());

		} catch (Exception exp) {
			LOG.error("Error occurred while generating address");
			throw new ApiException("Error occurred while generating address", exp);
		}

		clientAccount = new ClientAccount();
		clientAccount.setBtcAddress(addressDto.getUniqueBitcoinAddress());
		clientAccount.setEthAddress(addressDto.getUniqueEthereumAddress());
		clientAccount.setChildIndex(childIndex);
		clientAccount.setClientCorrelationId(generateAddressRequest.getClientCorrelationId());

		// Save into database
		try {
			clientAccountRepo.save(clientAccount);

		} catch (Exception exp) {
			LOG.error("Error occurred while persisting company account details in database");
			throw new ApiException("Error while persisting company account details", exp);
		}

		LOG.debug("Account details persisted in database . . .");
		generateAddressResponse = new GenerateAddressResponse(
				Arrays.asList(new AccountDetails(RequestType.BTC.name(), addressDto.getUniqueBitcoinAddress(), null),
						new AccountDetails(RequestType.ETH.name(), addressDto.getUniqueEthereumAddress(), null)),
				HttpStatus.OK.value(), ResponseCode.SUCCESS.getCode(), null);

		return generateAddressResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#retrieveBalance(com.ireslab.
	 * coinclaim.model.AccountBalanceRequest)
	 */
	@Override
	public AccountBalanceResponse retrieveBalance(AccountBalanceRequest accountBalanceRequest) {

		AccountBalanceResponse accountBalanceResponse = null;

		// Validating Api Request
		validateBaseApiRequest(accountBalanceRequest);

		String clientCorrelationId = accountBalanceRequest.getClientCorrelationId();

		ClientAccount clientAccount = clientAccountRepo.findByClientCorrelationId(clientCorrelationId);
		if (clientAccount == null || clientAccount.getBtcAddress() == null) {

			LOG.error("Client doesn't exists for ClientCorrelationId - " + clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.CLIENT_DOES_NOT_EXISTS.getCode(),
							"Client doesn't exists for Correlation Id : " + clientCorrelationId)));
		}

		TransactionDto transactionDto = null;
		List<AccountDetails> accountDetailsList = new ArrayList<>();

		try {
			LOG.debug("Getting account balance for BTC Address - " + clientAccount.getBtcAddress());
			transactionDto = bitcoinTxnService.retrieveBalance(clientAccount.getBtcAddress());

			BigDecimal balance = new BigDecimal(transactionDto.getAmount()).divide(AppConstants.BTC_DECIMAL_DIV);
			LOG.debug("Account Balance for address - '" + transactionDto.getFromAddress() + "' is - " + balance);
			accountDetailsList
					.add(new AccountDetails(RequestType.BTC.name(), clientAccount.getBtcAddress(), balance.toString()));

		} catch (Exception exp) {
			LOG.error("Error occurred while retrieving balance");
			throw new ApiException("Error occurred while retrieving balance", exp);
		}

		try {
			LOG.debug("Getting account balance for ETH Address - " + clientAccount.getEthAddress());
			transactionDto = ethereumTxnService.retrieveBalance(clientAccount.getEthAddress());

			BigDecimal balance = new BigDecimal(transactionDto.getAmount()).divide(AppConstants.ETH_DECIMAL_DIV);
			LOG.debug("Account Balance for address - '" + transactionDto.getFromAddress() + "' is - " + balance);
			accountDetailsList
					.add(new AccountDetails(RequestType.ETH.name(), clientAccount.getEthAddress(), balance.toString()));

		} catch (Exception exp) {
			LOG.error("Error occurred while retrieving balance");
			throw new ApiException("Error occurred while retrieving balance", exp);
		}

		accountBalanceResponse = new AccountBalanceResponse(accountDetailsList, HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), null);

		return accountBalanceResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#transferTokens(com.ireslab.
	 * coinclaim.model.TransferTokensRequest)
	 */
	@Override
	public TransferTokensResponse transferTokens(TransferTokensRequest transferTokensRequest) {

		TransferTokensResponse transferTokensResponse = null;

		// Validating Api Request
		validateBaseApiRequest(transferTokensRequest);

		if (transferTokensRequest.getBeneficiaryAddress() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(new Error(
					ResponseCode.MISSING_OR_INVALID_BENEFICIARY_ADDRESS.getCode(),
					"Missing or Invalid beneficiary address : " + transferTokensRequest.getBeneficiaryAddress())));
		}

		String noOfTokens = transferTokensRequest.getNoOfTokens();
		if (noOfTokens == null || new BigDecimal(noOfTokens).floatValue() < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_AMOUNT.getCode(), "Invalid Token Amount : " + noOfTokens)));
		}

		String clientCorrelationId = transferTokensRequest.getClientCorrelationId();
		ClientAccount clientAccount = clientAccountRepo.findByClientCorrelationId(clientCorrelationId);

		if (clientAccount == null || clientAccount.getBtcAddress() == null) {
			LOG.error("Client doesn't exists for ClientCorrelationId - " + clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.CLIENT_DOES_NOT_EXISTS.getCode(),
							"Client doesn't exists for Correlation Id : " + clientCorrelationId)));
		}

		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setIndex(clientAccount.getChildIndex().intValue());
		transactionDto.setToAddress(transferTokensRequest.getBeneficiaryAddress());

		// Bitcoin (BTC) transfer request
		if (transferTokensRequest.getType().equalsIgnoreCase(RequestType.BTC.name())) {

			String clientBtcAddress = clientAccount.getBtcAddress();
			transactionDto.setFromAddress(clientBtcAddress);

			BigInteger amountInSatoshi = new BigDecimal(noOfTokens).multiply(AppConstants.BTC_DECIMAL_DIV)
					.toBigInteger();
			transactionDto.setAmount(amountInSatoshi);

			try {
				LOG.debug("Initiating transfer of '" + amountInSatoshi + "' satoshis From : '" + clientBtcAddress
						+ "' , To : " + transferTokensRequest.getBeneficiaryAddress());
				transactionDto = bitcoinTxnService.transferTokens(transactionDto);
				LOG.debug("Bitcoins transferred successfully - " + transactionDto.getTransactionReciept());

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring bitcoins");
				throw new ApiException("Error occurred while transferring bitcoins", exp);
			}

			try {
				String balance = String
						.valueOf(new BigDecimal(bitcoinTxnService.retrieveBalance(clientBtcAddress).getAmount())
								.divide(AppConstants.BTC_DECIMAL_DIV).doubleValue());
				LOG.debug("Updated Bitcoin balance for client is  - " + balance);
				// transferTokensResponse.setAccountDetails(new AccountDetails.);

			} catch (Exception exp) {
				LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
			}

			// Ethereum (BTC) transfer request
		} else if (transferTokensRequest.getType().equalsIgnoreCase(RequestType.ETH.name())) {

			String clientEthAddress = clientAccount.getEthAddress();
			transactionDto.setFromAddress(clientEthAddress);

			BigInteger amountInWei = new BigDecimal(noOfTokens).multiply(AppConstants.ETH_DECIMAL_DIV).toBigInteger();
			transactionDto.setAmount(amountInWei);

			try {
				LOG.debug("Initiating transfer of '" + amountInWei + "' wei From : '" + clientEthAddress + "' , To : "
						+ transferTokensRequest.getBeneficiaryAddress());
				transactionDto = ethereumTxnService.transferTokens(transactionDto);
				LOG.debug("Ethers transferred successfully - " + transactionDto.getTransactionReciept());

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ethers");
				throw new ApiException("Error occurred while transferring ethers", exp);
			}

			try {
				String balance = String
						.valueOf(new BigDecimal(ethereumTxnService.retrieveBalance(clientEthAddress).getAmount())
								.divide(AppConstants.ETH_DECIMAL_DIV).doubleValue());
				LOG.debug("Updated Ethereum balance for client is  - " + balance);
				// transferTokensResponse.setAccountDetails(new AccountDetails.);

			} catch (Exception exp) {
				LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
			}
		}

		transferTokensResponse = new TransferTokensResponse(HttpStatus.OK.value(), ResponseCode.SUCCESS.getCode(),
				"Success");

		return transferTokensResponse;
	}

	/**
	 * @param apiRequest
	 */
	private void validateBaseApiRequest(BaseApiRequest apiRequest) {

		if (apiRequest == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_REQUEST.getCode(), "Invalid request")));

		} else if (apiRequest.getClientCorrelationId() == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.MISSING_OR_INVALID_CLIENT_CORRELATION_ID.getCode(),
							"Missing or Invalid ClientCorrelationId : " + apiRequest.getClientCorrelationId())));
		}
	}
}
