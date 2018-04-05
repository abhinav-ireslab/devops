package com.ireslab.coinclaim.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.entity.CompanyAccount;
import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TransferTokensRequest;
import com.ireslab.coinclaim.model.TransferTokensResponse;
import com.ireslab.coinclaim.repository.CompanyAccountRepo;
import com.ireslab.coinclaim.service.BlockchainTransactionService;
import com.ireslab.coinclaim.service.CcApiService;
import com.ireslab.coinclaim.service.CommonService;
import com.ireslab.coinclaim.utils.AppConstants;
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
	private CompanyAccountRepo companyAccountRepo;

	@Autowired
	private BlockchainTransactionService blockchainTxnService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#generateAddress(com.ireslab.
	 * coinclaim.model.GenerateAddressRequest)
	 */
	@Override
	public GenerateAddressResponse generateAddress(GenerateAddressRequest generateAddressRequest) {

		GenerateAddressResponse generateAddressResponse = null;

		// Create unique index
		BigInteger childIndex = commonService.getUniqueAddressIndex();

		// Call node service
		AddressDto addressDto = blockchainTxnService.generateAddress(childIndex);
		LOG.debug("Unique Bitcoin (BTC) Address generated - " + addressDto.getUniqueBitcoinAddress()
				+ ", for child index - " + childIndex);

		CompanyAccount companyAccount = new CompanyAccount();
		companyAccount.setBtcAddress(addressDto.getUniqueBitcoinAddress());
		companyAccount.setChildIndex(childIndex);
		companyAccount.setCompanyCorrelationId(generateAddressRequest.getCompanyCorrelationId());

		// Save into database
		companyAccountRepo.save(companyAccount);

		LOG.debug("Account details persisted in database");

		generateAddressResponse = new GenerateAddressResponse(HttpStatus.OK.value(), ResponseCode.Success.getCode(),
				"Success");
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

		CompanyAccount companyAccount = companyAccountRepo
				.findByCompanyCorrelationId(accountBalanceRequest.getCompanyCorrelationId());
		LOG.debug("Company Account - " + companyAccount.toString());

		TransactionDto transactionDto = blockchainTxnService.retrieveBalance(companyAccount.getBtcAddress());

		BigDecimal balance = new BigDecimal(transactionDto.getAmount()).divide(AppConstants.BTC_DECIMAL_DIV);
		LOG.debug("Account Balance for address - '" + transactionDto.getFromAddress() + "' is - " + balance);

		accountBalanceResponse = new AccountBalanceResponse(HttpStatus.OK.value(), ResponseCode.Success.getCode(),
				"Success");
		accountBalanceResponse.setAccountBalance(balance.toString());

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

		CompanyAccount companyAccount = companyAccountRepo
				.findByCompanyCorrelationId(transferTokensRequest.getCompanyCorrelationId());
		LOG.debug("Company Account - " + companyAccount.toString());

		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setFromAddress(companyAccount.getBtcAddress());
		transactionDto.setIndex(companyAccount.getChildIndex().intValue());
		transactionDto.setToAddress(transferTokensRequest.getBeneficiaryAddress());
		transactionDto.setAmount(new BigInteger(transferTokensRequest.getNoOfTokens()));

		transactionDto = blockchainTxnService.transferTokens(transactionDto);
		LOG.debug("Tokens transferred successfully - " + transactionDto.getTransactionReciept());

		transferTokensResponse = new TransferTokensResponse(HttpStatus.OK.value(), ResponseCode.Success.getCode(),
				"Success");
		transferTokensResponse.setAccountBalance(
				blockchainTxnService.retrieveBalance(companyAccount.getBtcAddress()).getAmount().toString());

		return transferTokensResponse;
	}
}
