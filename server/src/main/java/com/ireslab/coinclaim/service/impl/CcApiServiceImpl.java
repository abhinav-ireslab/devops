package com.ireslab.coinclaim.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TokenDetailsDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.entity.CompanyAccount;
import com.ireslab.coinclaim.entity.CompanyToken;
import com.ireslab.coinclaim.entity.UserAccount;
import com.ireslab.coinclaim.exception.ApiException;
import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.AccountDetails;
import com.ireslab.coinclaim.model.BaseApiRequest;
import com.ireslab.coinclaim.model.Error;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationRequest;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationResponse;
import com.ireslab.coinclaim.model.TokenTransferRequest;
import com.ireslab.coinclaim.model.TokenTransferResponse;
import com.ireslab.coinclaim.repository.CompanyAccountRepo;
import com.ireslab.coinclaim.repository.CompanyTokenRepo;
import com.ireslab.coinclaim.repository.UserAccountRepo;
import com.ireslab.coinclaim.service.BlockchainTransactionService;
import com.ireslab.coinclaim.service.CcApiService;
import com.ireslab.coinclaim.service.CommonService;
import com.ireslab.coinclaim.utils.AppConstants;
import com.ireslab.coinclaim.utils.ClientType;
import com.ireslab.coinclaim.utils.ResponseCode;
import com.ireslab.coinclaim.utils.TokenConfig;
import com.ireslab.coinclaim.utils.TokenType;

/**
 * @author iRESlab
 *
 */
@Service
public class CcApiServiceImpl implements CcApiService {

	private static final Logger LOG = LoggerFactory.getLogger(CcApiService.class);

	@Autowired
	private Web3j web3j;

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private CommonService commonService;

	@Autowired
	private TokenConfig clmTokenConfig;

	@Autowired
	private CompanyTokenRepo companyTokenRepo;

	@Autowired
	private CompanyAccountRepo companyAccountRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

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

		AddressDto addressDto = null;
		GenerateAddressResponse generateAddressResponse = null;

		// Validating Api Request
		validateBaseApiRequest(generateAddressRequest);

		String clientCorrelationId = generateAddressRequest.getClientCorrelationId();
		ClientType clientType = validateClientType(generateAddressRequest.getClientType());
		/*
		 * Request received for Company Address Generation
		 */
		if (clientType.equals(ClientType.COMPANY)) {
			CompanyAccount companyAccount = companyAccountRepo.findByCompanyCorrelationId(clientCorrelationId);

			if (companyAccount != null && companyAccount.getBtcAddress() != null) {
				LOG.error("Invalid correlation id | Client already exists with client correlation id - "
						+ clientCorrelationId);
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.COMPANY_ALREADY_EXISTS.getCode(),
								"Client already exists with Correlation Id")));
			}

			addressDto = generateAddresses(ClientType.COMPANY);

			companyAccount = new CompanyAccount();
			companyAccount.setBtcAddress(addressDto.getUniqueBitcoinAddress());
			companyAccount.setEthAddress(addressDto.getUniqueEthereumAddress());
			companyAccount.setChildIndex(addressDto.getIndex());
			companyAccount.setCompanyCorrelationId(clientCorrelationId);

			// Save into database
			try {
				companyAccountRepo.save(companyAccount);
				LOG.debug("Account details persisted in database . . .");

			} catch (Exception exp) {
				LOG.error("Error occurred while persisting company account details in database");
				throw new ApiException("Error while persisting company account details", exp);
			}
		}

		/*
		 * Request received for User Address Generation
		 */
		else if (clientType.equals(ClientType.USER)) {
			UserAccount userAccount = userAccountRepo.findByUserCorrelationId(clientCorrelationId);

			if (userAccount != null && userAccount.getBtcAddress() != null) {
				LOG.error("Invalid correlation id | User already exists with user correlation id - "
						+ clientCorrelationId);
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.USER_ALREADY_EXISTS.getCode(),
								"User already exists with Correlation Id")));
			}

			addressDto = generateAddresses(ClientType.USER);

			userAccount = new UserAccount();
			userAccount.setBtcAddress(addressDto.getUniqueBitcoinAddress());
			userAccount.setEthAddress(addressDto.getUniqueEthereumAddress());
			userAccount.setChildIndex(addressDto.getIndex());
			userAccount.setUserCorrelationId(clientCorrelationId);

			// Save into database
			try {
				userAccountRepo.save(userAccount);
				LOG.debug("Account details persisted in database . . .");

			} catch (Exception exp) {
				LOG.error("Error occurred while persisting user account details in database");
				throw new ApiException("Error while persisting company account details", exp);
			}

		} else {
			LOG.error("Invalid client type received in request");
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(),
							"Invalid Client Type'" + clientType + "'received in request")));
		}

		generateAddressResponse = new GenerateAddressResponse(
				Arrays.asList(new AccountDetails(TokenType.BTC.name(), addressDto.getUniqueBitcoinAddress(), null),
						new AccountDetails(TokenType.ETH.name(), addressDto.getUniqueEthereumAddress(), null)),
				HttpStatus.OK.value(), ResponseCode.SUCCESS.getCode(), null);

		return generateAddressResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#transferTokens(com.ireslab.
	 * coinclaim.model.TransferTokensRequest)
	 */
	@Override
	public TokenTransferResponse transferTokens(TokenTransferRequest transferTokensRequest) {

		TokenTransferResponse transferTokensResponse = null;

		String companyCorrelationId = transferTokensRequest.getClientCorrelationId();
		String userCorrelationId = transferTokensRequest.getUserCorrelationId();
		String beneficiaryAddress = transferTokensRequest.getBeneficiaryAddress();

		TokenType tokenType = validateTokenType(transferTokensRequest.getTokenType());
		String tokenSymbol = transferTokensRequest.getTokenSymbol();

		// Validate noOfTokens
		String noOfTokens = transferTokensRequest.getNoOfTokens();
		if (noOfTokens == null || new BigDecimal(noOfTokens).floatValue() < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_AMOUNT.getCode(), "Invalid Token Amount : " + noOfTokens)));
		}

		UserAccount userAccount = null;
		TokenConfig tokenConfig = null;

		String senderAddress = null;
		String receiverAddress = null;

		/**
		 * Company to User Account/Beneficiary address (BTC/ETH/ERC20)
		 */
		if (companyCorrelationId != null) {

			validateBaseApiRequest(transferTokensRequest);
			CompanyAccount companyAccount = getCompanyAccount(transferTokensRequest.getClientCorrelationId());

			// Transfer from COMPANY Account to USER Account
			if (userCorrelationId != null) {
				userAccount = getUserAccount(transferTokensRequest.getUserCorrelationId());
			}

			// Transfer from COMPANY Account to USER Address
			else if (userCorrelationId == null && beneficiaryAddress != null) {
				userAccount = new UserAccount();
				userAccount.setBtcAddress(beneficiaryAddress);
				userAccount.setEthAddress(beneficiaryAddress);

			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.INVALID_REQUEST.getCode(), "Invalid Request")));
			}

			switch (tokenType) {
			case BTC:
				senderAddress = companyAccount.getBtcAddress();
				receiverAddress = userAccount.getBtcAddress();
				break;

			case ETH:
				senderAddress = companyAccount.getEthAddress();
				receiverAddress = userAccount.getEthAddress();
				break;

			case ERC20:
				senderAddress = companyAccount.getEthAddress();
				receiverAddress = userAccount.getEthAddress();

				CompanyToken companyToken = companyTokenRepo.findByTokenSymbolAndCompanyAccount_CompanyAccountId(
						tokenSymbol, companyAccount.getCompanyAccountId());

				if (companyToken == null) {
					LOG.error("Invalid Token Symbol | Token '" + tokenSymbol
							+ " 'doesn't exists for Company with Correlation Id - " + companyCorrelationId);
					throw new ApiException(HttpStatus.BAD_REQUEST,
							Arrays.asList(new Error(ResponseCode.TOKEN_DOES_NOT_EXISTS.getCode(),
									"Token '" + tokenSymbol + " 'doesn't exists for Company with Correlation Id - "
											+ companyCorrelationId)));
				}

				// Getting Company's or user's account Private Key
				String privateKey = ethereumTxnService
						.derivePrivateKey(companyAccount.getChildIndex(), ClientType.COMPANY)
						.getEthereumAddressPrivateKey();

				tokenConfig = new TokenConfig();
				tokenConfig.setTokenSymbol(tokenSymbol);
				tokenConfig.setTokenDeployerPrivateKey(privateKey);
				tokenConfig.setTokenDecimal(companyToken.getTokenDecimals().toString());
				tokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
				tokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
				break;

			default:
				break;
			}

			transferTokensRequest.setClientType(ClientType.COMPANY.name());
			transferTokensRequest.setSenderAddress(senderAddress);
			transferTokensRequest.setSenderAddressIndex(companyAccount.getChildIndex());
			transferTokensRequest.setBeneficiaryAddress(receiverAddress);

			transferTokensResponse = transferTokens(transferTokensRequest, tokenConfig);
		}

		/**
		 * Transfer from either
		 * 
		 * - User Account to User Address (BTC/ETH/ERC20/CLM)
		 * 
		 * - CLM account to User Account (CLM)
		 */
		else {

			// User Account to User Address (BTC/ETH/ERC20/CLM)
			if (userCorrelationId != null && beneficiaryAddress != null) {
				userAccount = getUserAccount(transferTokensRequest.getUserCorrelationId());
				receiverAddress = beneficiaryAddress;
				tokenConfig = new TokenConfig();

				switch (tokenType) {
				case BTC:
					senderAddress = userAccount.getBtcAddress();
					break;

				case ETH:
					senderAddress = userAccount.getEthAddress();
					break;

				case ERC20:
					senderAddress = userAccount.getEthAddress();

					// Not CLM
					if (!tokenSymbol.equalsIgnoreCase(clmTokenConfig.getTokenSymbol())) {

						CompanyToken companyToken = companyTokenRepo.findByTokenSymbol(tokenSymbol);

						if (companyToken == null) {
							LOG.error("Invalid Token Symbol | Token '" + tokenSymbol + " 'doesn't exists");
							throw new ApiException(HttpStatus.BAD_REQUEST,
									Arrays.asList(new Error(ResponseCode.TOKEN_DOES_NOT_EXISTS.getCode(),
											"Token '" + tokenSymbol + " 'doesn't exists")));
						}
						tokenConfig.setTokenSymbol(tokenSymbol);
						tokenConfig.setTokenDecimal(companyToken.getTokenDecimals().toString());
						tokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
						tokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					}

					// CLM
					else {
						try {
							tokenConfig = (TokenConfig) clmTokenConfig.clone();
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}

				// Getting Company's or user's account Private Key
				String privateKey = ethereumTxnService.derivePrivateKey(userAccount.getChildIndex(), ClientType.USER)
						.getEthereumAddressPrivateKey();
				tokenConfig.setTokenDeployerPrivateKey(privateKey);

				transferTokensRequest.setSenderAddress(senderAddress);
				transferTokensRequest.setSenderAddressIndex(userAccount.getChildIndex());

			} else if (userCorrelationId != null && beneficiaryAddress == null
					&& tokenSymbol.equalsIgnoreCase(clmTokenConfig.getTokenSymbol())) {

				userAccount = getUserAccount(transferTokensRequest.getUserCorrelationId());
				receiverAddress = userAccount.getEthAddress();

				// CLM to User Address (CLM)
				try {
					tokenConfig = (TokenConfig) clmTokenConfig.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}

			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.INVALID_REQUEST.getCode(), "Invalid Request")));
			}

			transferTokensRequest.setClientType(ClientType.USER.name());
			transferTokensRequest.setBeneficiaryAddress(receiverAddress);
			transferTokensResponse = transferTokens(transferTokensRequest, tokenConfig);
		}

		return transferTokensResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#saveTokenDetails(com.ireslab.
	 * coinclaim.model.TokenDetailsRegistrationRequest)
	 */
	@Override
	public TokenDetailsRegistrationResponse saveTokenDetails(
			TokenDetailsRegistrationRequest tokenDetailsRegistrationRequest) {

		TokenDetailsRegistrationResponse tokenDetailsRegistrationResponse = null;

		String clientCorrelationId = tokenDetailsRegistrationRequest.getClientCorrelationId();

		// Validating request
		validateBaseApiRequest(tokenDetailsRegistrationRequest);
		validateTokenDetails(tokenDetailsRegistrationRequest);

		// Get company details based on correlation id
		CompanyAccount companyAccount = getCompanyAccount(clientCorrelationId);

		TokenDetailsDto tokenDetailsDto = ethereumTxnService
				.checkTokenDetails(tokenDetailsRegistrationRequest.getTokenContractAddress());

		if (null != tokenDetailsDto.getErrorCode() && tokenDetailsDto
				.getErrorCode() == ResponseCode.TOKEN_CONTRACT_ADDRESS_INVALID.getCode().longValue()) {

			LOG.error("Result Code - " + tokenDetailsDto.getResultCode() + " | Description - "
					+ tokenDetailsDto.getDescription() + " | Error Code - " + tokenDetailsDto.getErrorCode());
			throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.TOKEN_CONTRACT_ADDRESS_INVALID.getCode(),
					"Invalid token contract address.",
					Arrays.asList(new Error(ResponseCode.TOKEN_CONTRACT_ADDRESS_INVALID.getCode(),
							ResponseCode.TOKEN_CONTRACT_ADDRESS_INVALID.toString())));
		}

		Integer tokenDecimal = Integer.parseInt(tokenDetailsDto.getTokenDecimal());
		String tokenContractAddress = String.valueOf(tokenDetailsDto.getContractABI());

		CompanyToken companyToken = new CompanyToken();
		companyToken.setCompanyAccount(companyAccount);
		companyToken.setTokenName(tokenDetailsDto.getTokenName());
		companyToken.setTokenSymbol(tokenDetailsDto.getTokenSymbol());
		companyToken.setTokenDecimals(new BigInteger(
				StringUtils.rightPad(BigInteger.ONE.toString(), (tokenDecimal + 1), BigInteger.ZERO.toString())));
		companyToken.setTokenContractAddress(tokenDetailsDto.getTokenContractAddress());
		companyToken.setTokenContractBinary(tokenContractAddress);

		// Save into database
		try {
			companyTokenRepo.save(companyToken);
			LOG.debug("Company token details persisted in database . . .");

		} catch (DataIntegrityViolationException dexp) {
			LOG.error("Token with token symbol - '" + tokenDetailsDto.getTokenSymbol()
					+ "' already exists for client with correlationId - " + clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.TOKEN_ALREADY_EXISTS.getCode(),
							"Token with token symbol '" + tokenDetailsDto.getTokenSymbol()
									+ "' already exits for Client Correlation Id - " + clientCorrelationId)));

		} catch (Exception exp) {
			LOG.error("Error occurred while persisting company token details in database");
			throw new ApiException("Error while persisting company account details", exp);
		}

		tokenDetailsRegistrationResponse = new TokenDetailsRegistrationResponse(HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(),
				"Token Details for token '" + tokenDetailsDto.getTokenSymbol() + "' successfully saved");

		return tokenDetailsRegistrationResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#retrieveBalance(com.ireslab.
	 * coinclaim.model.AccountBalanceRequest)
	 */
	@Override
	public AccountBalanceResponse retrieveBalance(AccountBalanceRequest accountBalanceRequest) {

		final String ethereumAddress;
		AccountBalanceResponse accountBalanceResponse = null;

		List<Error> errors = new ArrayList<>();
		List<AccountDetails> accountDetailsList = new ArrayList<>();

		String clientCorrelationId = accountBalanceRequest.getClientCorrelationId();

		// Check Invalid Client Type
		ClientType clientType = null;
		try {
			clientType = ClientType.valueOf(accountBalanceRequest.getClientType());
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(),
							"Invalid Client Type'" + clientType + "'received in request")));
		}

		// Validating Api Request
		validateBaseApiRequest(accountBalanceRequest);

		/*
		 * COMPANY Account Balance Request
		 */
		if (clientType.equals(ClientType.COMPANY)) {

			CompanyAccount companyAccount = getCompanyAccount(clientCorrelationId);
			ethereumAddress = companyAccount.getEthAddress();

			// BTC Account balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), companyAccount.getBtcAddress(),
						retrieveBitcoinBalance(companyAccount.getBtcAddress()).toString()));

			} catch (Exception exp) {
				LOG.error("Failed to get BTC balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get BTC balance"));
			}

			// ETH Account balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), ethereumAddress,
						retrieveEthereumBalance(ethereumAddress).toString()));

			} catch (Exception exp) {
				LOG.error("Failed to get ETH balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get ETH balance"));
			}

			// ERC20 Account balance
			LOG.debug("Getting account balances for ERC20 tokens for address - " + ethereumAddress);

			List<CompanyToken> companyTokens = companyAccount.getCompanyTokens();
			companyTokens.forEach(companyToken -> {

				TokenConfig erc20TokenConfig = null;

				try {
					erc20TokenConfig = new TokenConfig();
					erc20TokenConfig.setTokenSymbol(companyToken.getTokenSymbol());
					erc20TokenConfig.setTokenDecimal(companyToken.getTokenDecimals().toString());
					erc20TokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					erc20TokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
					erc20TokenConfig.setTokenDeployerPrivateKey("e");

					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress,
							retrieveERC20Balance(ethereumAddress, erc20TokenConfig))
									.setTokenCode(companyToken.getTokenSymbol()));
				} catch (Exception exp) {
					LOG.error("Failed to get '" + erc20TokenConfig.getTokenSymbol() + "' token balance - "
							+ ExceptionUtils.getStackTrace(exp));
					errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
							"Failed to get '" + erc20TokenConfig.getTokenSymbol() + "' token balance"));
				}
			});
		}

		/*
		 * USER Account Balance Request
		 */
		else if (clientType.equals(ClientType.USER)) {

			UserAccount userAccount = getUserAccount(clientCorrelationId);
			ethereumAddress = userAccount.getEthAddress();

			// BTC Account balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), userAccount.getBtcAddress(),
						retrieveBitcoinBalance(userAccount.getBtcAddress()).toString()));
			} catch (Exception exp) {
				LOG.error("Failed to get BTC balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get BTC balance"));
			}

			// ETH Account balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), ethereumAddress,
						retrieveEthereumBalance(ethereumAddress).toString()));
			} catch (Exception exp) {
				LOG.error("Failed to get ETH balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get ETH balance"));
			}

			// ERC20-CLM Account balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress,
						retrieveERC20Balance(ethereumAddress, clmTokenConfig))
								.setTokenCode(clmTokenConfig.getTokenSymbol()));

			} catch (Exception exp) {
				LOG.error("Failed to get '" + clmTokenConfig.getTokenSymbol() + "' token balance - "
						+ ExceptionUtils.getStackTrace(exp));
				errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
						"Failed to get '" + clmTokenConfig.getTokenSymbol() + "' token balance"));
			}

			// ERC-20 Account Balance
			companyTokenRepo.findAll().forEach(erc20CompanyToken -> {

				TokenConfig erc20TokenConfig = new TokenConfig();
				erc20TokenConfig.setTokenSymbol(erc20CompanyToken.getTokenSymbol());
				erc20TokenConfig.setTokenDecimal(erc20CompanyToken.getTokenDecimals().toString());
				erc20TokenConfig.setTokenContractAddress(erc20CompanyToken.getTokenContractAddress());
				erc20TokenConfig.setTokenContractBinary(erc20CompanyToken.getTokenContractBinary());
				erc20TokenConfig.setTokenDeployerPrivateKey("e");

				try {
					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress,
							retrieveERC20Balance(ethereumAddress, erc20TokenConfig).toString())
									.setTokenCode(erc20CompanyToken.getTokenSymbol()));

				} catch (Exception exp) {
					LOG.error("Failed to get '" + erc20TokenConfig.getTokenSymbol() + "' token balance - "
							+ ExceptionUtils.getStackTrace(exp));
					errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
							"Failed to get '" + erc20TokenConfig.getTokenSymbol() + "' token balance"));
				}
			});
		}

		accountBalanceResponse = new AccountBalanceResponse(accountDetailsList, HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), errors);
		accountBalanceResponse.setAccountDetails(accountDetailsList);

		return accountBalanceResponse;
	}

	/**
	 * @param tokenTransferRequest
	 * @param tokenConfig
	 * @return
	 */
	private TokenTransferResponse transferTokens(TokenTransferRequest tokenTransferRequest, TokenConfig tokenConfig) {

		String successMessage = null;
		TokenTransferResponse tokenTransferResponse = null;

		String noOfTokens = tokenTransferRequest.getNoOfTokens();
		String senderAccountAddress = tokenTransferRequest.getSenderAddress();
		String receiverAccountAddress = tokenTransferRequest.getBeneficiaryAddress();
		String tokenSymbol = tokenTransferRequest.getTokenSymbol();

		List<Error> errors = new ArrayList<>();
		List<AccountDetails> accountDetailsList = new ArrayList<>();

		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setClientType(tokenTransferRequest.getClientType());
		transactionDto.setGasPrice(tokenTransferRequest.getGasPrice());

		switch (TokenType.valueOf(tokenTransferRequest.getTokenType())) {
		case BTC:
			BigInteger amountInSatoshi = new BigDecimal(noOfTokens).multiply(AppConstants.BTC_DECIMAL_DIV)
					.toBigInteger();

			transactionDto.setIndex(tokenTransferRequest.getSenderAddressIndex().intValue());
			transactionDto.setAmount(amountInSatoshi);
			transactionDto.setFromAddress(senderAccountAddress);
			transactionDto.setToAddress(receiverAccountAddress);

			try {
				LOG.debug("Initiating transfer of '" + amountInSatoshi + "' satoshis From : '" + senderAccountAddress
						+ "' , To : " + receiverAccountAddress);

				transactionDto = bitcoinTxnService.transferTokens(transactionDto);
				LOG.debug("Response from node server - " + transactionDto.toString());

				if (transactionDto.getTransactionReciept() == null) {
					throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
							Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
									"Error occurred while transferring bitcoins - [Error Code - "
											+ transactionDto.getDescription() + ", Description - "
											+ transactionDto.getDescription() + "]")));
				}

				LOG.debug("Bitcoins transferred successfully - " + transactionDto.getTransactionReciept());

				successMessage = noOfTokens + " Bitcoins (BTC) successfully transferred";

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring bitcoins - " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
						Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
								"Error occurred while transferring bitcoins")));
			}

			// Getting updated BTC balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), senderAccountAddress,
						retrieveBitcoinBalance(senderAccountAddress).toString()));

			} catch (Exception exp) {
				LOG.error("Failed to get BTC balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get BTC balance"));
			}
			break;

		case ETH:
			BigInteger amountInWei = new BigDecimal(noOfTokens).multiply(AppConstants.ETH_DECIMAL_DIV).toBigInteger();

			transactionDto.setIndex(tokenTransferRequest.getSenderAddressIndex().intValue());

			transactionDto.setAmount(amountInWei);
			transactionDto.setFromAddress(senderAccountAddress);
			transactionDto.setToAddress(receiverAccountAddress);

			try {
				validateGasPrice(tokenTransferRequest);

				BigInteger gasPrice = null;

				if (tokenTransferRequest.getGasPrice() != null && !tokenTransferRequest.getGasPrice().isEmpty()) {
					gasPrice = new BigInteger(tokenTransferRequest.getGasPrice());

					// Conversion of GWEI to WEI
					gasPrice = gasPrice.divide(AppConstants.GWEI_TO_WEI);
					transactionDto.setGasPrice(gasPrice.toString());
				}

				LOG.debug("Initiating transfer of '" + amountInWei + "' wei From : '" + senderAccountAddress
						+ "' , To : " + receiverAccountAddress);
				transactionDto = ethereumTxnService.transferTokens(transactionDto);

				if (transactionDto.getErrorCode() != null) {
					LOG.error("Result Code - " + transactionDto.getResultCode() + " | Description - "
							+ transactionDto.getDescription() + " | Error Code - " + transactionDto.getErrorCode());
					throw new ApiException("Result Code - " + transactionDto.getResultCode() + " | Description - "
							+ transactionDto.getDescription() + " | Error Code - " + transactionDto.getErrorCode());
				}

				LOG.debug("Ethers transferred successfully - " + transactionDto.getTransactionReciept());

				successMessage = noOfTokens + " Ethers (ETH) successfully transferred";

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ethers");

				if (exp instanceof ApiException) {
					throw (ApiException) exp;
				}
				throw new ApiException("Error occurred while transferring ethers", exp);
			}

			// Getting updated ETH balance
			try {
				accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), senderAccountAddress,
						retrieveEthereumBalance(senderAccountAddress).toString()));

			} catch (Exception exp) {
				LOG.error("Failed to get ETH balance - " + ExceptionUtils.getStackTrace(exp));
				errors.add(
						new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(), "Failed to get ETH balance"));
			}
			break;

		case ERC20:
			BigInteger tokenQuantity = new BigDecimal(noOfTokens)
					.multiply(new BigDecimal(tokenConfig.getTokenDecimal())).toBigInteger();

			boolean isCoinClaimTokenTransfer = tokenConfig.getTokenSymbol()
					.equalsIgnoreCase(clmTokenConfig.getTokenSymbol());

			try {
				LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenSymbol + "' tokens , To Address : "
						+ receiverAccountAddress);

				// if (!isCoinClaimTokenTransfer) {
				// // Getting Company's or user's account Private Key
				// String privateKey = ethereumTxnService
				// .derivePrivateKey(tokenTransferRequest.getSenderAddressIndex(),
				// ClientType.valueOf(tokenTransferRequest.getClientType()))
				// .getEthereumAddressPrivateKey();
				//
				// tokenConfig.setTokenDeployerPrivateKey(privateKey);
				// }

				validateGasLimit(tokenTransferRequest);
				validateGasPrice(tokenTransferRequest);
				TokenContractService tokenContractService = null;

				if (tokenTransferRequest.getGasPrice() != null && tokenTransferRequest.getGasLimit() != null) {

					BigInteger gasPrice = new BigInteger(tokenTransferRequest.getGasPrice());
					BigInteger gasLimit = new BigInteger(tokenTransferRequest.getGasLimit());

					// Conversion of wei to gwei
					gasPrice = gasPrice.divide(new BigInteger("1000000000"));

					if (tokenTransferRequest.getGasPrice().isEmpty() && tokenTransferRequest.getGasLimit().isEmpty()) {
						tokenContractService = TokenContractService.getContractServiceInstance(web3j, tokenConfig);
					} else if (!tokenTransferRequest.getGasPrice().isEmpty()
							&& !tokenTransferRequest.getGasLimit().isEmpty()) {

						tokenContractService = TokenContractService.getContractServiceInstance(web3j, tokenConfig,
								gasPrice, gasLimit);

					} else if (tokenTransferRequest.getGasPrice().isEmpty()
							&& !tokenTransferRequest.getGasLimit().isEmpty()) {

						tokenContractService = TokenContractService.getContractServiceInstance(web3j, tokenConfig,
								Contract.GAS_PRICE, gasLimit);

					} else if (!tokenTransferRequest.getGasPrice().isEmpty()
							&& tokenTransferRequest.getGasLimit().isEmpty()) {

						tokenContractService = TokenContractService.getContractServiceInstance(web3j, tokenConfig,
								gasPrice, Contract.GAS_LIMIT);
					}
				} else {
					tokenContractService = TokenContractService.getContractServiceInstance(web3j, tokenConfig);
				}

				TransactionReceipt transactionReceipt = tokenContractService.allocateTokens(receiverAccountAddress,
						tokenQuantity);

				if (transactionReceipt != null) {
					LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

					if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
						throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
					}
				}

				successMessage = noOfTokens + " no of '" + tokenSymbol + "' tokens successfully transferred";

				// Not CLM
				if (!isCoinClaimTokenTransfer) {

					// ERC20 Account Balance
					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), senderAccountAddress,
							String.valueOf(new BigDecimal(tokenContractService.retrieveBalance(senderAccountAddress))
									.divide(new BigDecimal(tokenConfig.getTokenDecimal())).doubleValue()))
											.setTokenCode(tokenConfig.getTokenSymbol()));
				}

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));

				if (exp.getMessage().indexOf("intrinsic gas too low") != -1) {

					Error error = new Error(ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(), "Intrinsic gas too low");

					throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(),
							HttpStatus.INTERNAL_SERVER_ERROR.name(), Arrays.asList(error));

				} else if (exp.getMessage().indexOf("exceeds block gas limit") != -1) {

					Error error = new Error(ResponseCode.GAS_LIMIT_EXCEEDS.getCode(), "Exceeds block gas limit");

					throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.GAS_LIMIT_EXCEEDS.getCode(),
							HttpStatus.INTERNAL_SERVER_ERROR.name(), Arrays.asList(error));
				}

				throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
						Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
								"Error occurred while transferring ERC20 tokens - " + tokenSymbol)));
			}
			break;

		default:
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_TYPE.getCode(), "Invalid Token Type received in request")));
		}

		tokenTransferResponse = new TokenTransferResponse(HttpStatus.OK.value(), ResponseCode.SUCCESS.getCode(),
				successMessage, errors);
		tokenTransferResponse.setAccountDetails(accountDetailsList);

		return tokenTransferResponse;
	}

	/**
	 * @param clientType
	 * @return
	 */
	private ClientType validateClientType(String clientType) {

		ClientType clientTypeEnum = null;
		try {
			clientTypeEnum = ClientType.valueOf(clientType);
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(),
							"Invalid Client Type'" + clientType + "'received in request")));
		}
		return clientTypeEnum;
	}

	/**
	 * @param tokenType
	 * @return
	 */
	private TokenType validateTokenType(String tokenType) {

		TokenType tokenTypeEnum = null;

		try {
			tokenTypeEnum = TokenType.valueOf(tokenType);
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_TYPE.getCode(), "Invalid Token Type sent - " + tokenType)));
		}
		return tokenTypeEnum;
	}

	/**
	 * @param clientType
	 * @return
	 */
	private AddressDto generateAddresses(ClientType clientType) {

		AddressDto addressDto = null;

		// Create unique index
		BigInteger childIndex = commonService.getUniqueAddressIndex(clientType);
		LOG.debug("Unique child index generated - " + childIndex);

		// Call node service
		try {
			addressDto = bitcoinTxnService.generateAddress(childIndex, clientType);
			LOG.debug("Unique generated addresses for " + clientType + ":\n" + "\t Bitcoin (BTC) Address generated - "
					+ addressDto.getUniqueBitcoinAddress() + "\n\t Unique Ethereum (ETH) Address generated - "
					+ addressDto.getUniqueEthereumAddress());

			addressDto.setIndex(childIndex);

		} catch (Exception exp) {
			LOG.error("Error occurred while generating address");
			throw new ApiException("Error occurred while generating address", exp);
		}
		return addressDto;
	}

	/**
	 * @param ethAddress
	 * @param erc20TokenConfig
	 * @return
	 */
	private String retrieveERC20Balance(String ethAddress, TokenConfig erc20TokenConfig) {

		TokenContractService erc20TokenContractService = TokenContractService.getContractServiceInstance(web3j,
				erc20TokenConfig);

		String erc20TokenBalance = String.valueOf(new BigDecimal(erc20TokenContractService.retrieveBalance(ethAddress))
				.divide(new BigDecimal(erc20TokenConfig.getTokenDecimal())).doubleValue());

		return erc20TokenBalance;
	}

	/**
	 * @param btcAddress
	 * @return
	 */
	private BigDecimal retrieveBitcoinBalance(String btcAddress) {

		try {
			LOG.debug("Getting account balance for BTC Address - " + btcAddress);
			TransactionDto transactionDto = bitcoinTxnService.retrieveBalance(btcAddress);

			BigDecimal balance = new BigDecimal(transactionDto.getAmount()).divide(AppConstants.BTC_DECIMAL_DIV);
			LOG.debug("Account Balance for address - '" + transactionDto.getFromAddress() + "' is - " + balance);

			return balance;

		} catch (Exception exp) {
			LOG.error("Error occurred while retrieving balance");
			throw new ApiException("Error occurred while retrieving balance", exp);
		}
	}

	/**
	 * @param ethAddress
	 * @return
	 */
	private BigDecimal retrieveEthereumBalance(String ethAddress) {

		try {
			LOG.debug("Getting account balance for ETH Address - " + ethAddress);
			TransactionDto transactionDto = ethereumTxnService.retrieveBalance(ethAddress);

			BigDecimal balance = new BigDecimal(transactionDto.getAmount()).divide(AppConstants.ETH_DECIMAL_DIV);
			LOG.debug("Account Balance for address - '" + ethAddress + "' is - " + balance);

			return balance;

		} catch (Exception exp) {
			LOG.error("Error occurred while retrieving balance");
			throw new ApiException("Error occurred while retrieving balance", exp);
		}
	}

	/**
	 * @param tokenDetailsRegistrationRequest
	 */
	private void validateTokenDetails(TokenDetailsRegistrationRequest tokenDetailsRegistrationRequest) {

		/*
		 * if (StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenSymbol() ) ||
		 * StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenSymbol()) ||
		 * StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenDecimals( )) ||
		 * StringUtils.isBlank(tokenDetailsRegistrationRequest.
		 * getTokenContractAddress() ) ||
		 * StringUtils.isBlank(tokenDetailsRegistrationRequest.
		 * getTokenContractBinary()) ) { throw new ApiException(HttpStatus.BAD_REQUEST,
		 * Arrays.asList(new Error(ResponseCode.INVALID_TOKEN_DETAILS.getCode(),
		 * "Invalid Token Details"))); }
		 */
		if (StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenContractAddress())) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_TOKEN_DETAILS.getCode(), "Invalid Token Details")));
		}
	}

	/**
	 * @param userCorrelationId
	 * @return
	 */
	private UserAccount getUserAccount(String userCorrelationId) {

		UserAccount userAccount = userAccountRepo.findByUserCorrelationId(userCorrelationId);

		if (userAccount == null || userAccount.getBtcAddress() == null) {
			LOG.error("Invalid correlation id | User doesn't exists with Correlation Id - " + userCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.USER_DOES_NOT_EXISTS.getCode(),
							"User doesn't exists for Correlation Id -" + userCorrelationId)));
		}
		return userAccount;
	}

	/**
	 * @param clientCorrelationId
	 * @return
	 */
	private CompanyAccount getCompanyAccount(String clientCorrelationId) {

		CompanyAccount companyAccount = companyAccountRepo.findByCompanyCorrelationId(clientCorrelationId);

		if (companyAccount == null || companyAccount.getBtcAddress() == null) {
			LOG.error("Company doesn't exists for ClientCorrelationId - " + clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.COMPANY_DOES_NOT_EXISTS.getCode(),
							"Company doesn't exists for Correlation Id : " + clientCorrelationId)));
		}

		return companyAccount;
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

	/**
	 * @param tokenTransferRequest
	 * @throws Exception
	 */
	private void validateGasLimit(TokenTransferRequest tokenTransferRequest) throws Exception {

		if (!StringUtils.isBlank(tokenTransferRequest.getGasLimit())) {

			BigInteger gasLimit = new BigInteger(tokenTransferRequest.getGasLimit());

			if (gasLimit.intValue() < 21000)
				throw new Exception("intrinsic gas too low");
		}
	}

	/**
	 * @param tokenTransferRequest
	 * @throws Exception
	 */
	private void validateGasPrice(TokenTransferRequest tokenTransferRequest) throws Exception {

		if (StringUtils.isNotBlank(tokenTransferRequest.getGasPrice())) {

			BigInteger gasPrice = new BigInteger(tokenTransferRequest.getGasPrice());
			gasPrice = gasPrice.divide(AppConstants.GWEI_TO_WEI);

			if (gasPrice.compareTo(BigInteger.ZERO) == -1 || gasPrice.compareTo(BigInteger.ZERO) == 0) {
				throw new ApiException(HttpStatus.BAD_REQUEST, ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(),
						HttpStatus.INTERNAL_SERVER_ERROR.name(), Arrays.asList(
								new Error(ResponseCode.INTRINSIC_GAS_TOO_LOW.getCode(), "Intrinsic gas too low")));
			}
		}
	}
}
