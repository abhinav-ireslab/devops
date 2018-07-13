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

import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.coinclaim.dto.AddressDto;
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
import com.ireslab.coinclaim.utils.CLMTokenConfig;
import com.ireslab.coinclaim.utils.ClientType;
import com.ireslab.coinclaim.utils.ResponseCode;
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
	private CLMTokenConfig clmTokenConfig;

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

		ClientType clientType = null;
		String clientCorrelationId = generateAddressRequest.getClientCorrelationId();

		// Check Invalid Client Type
		try {
			clientType = ClientType.valueOf(generateAddressRequest.getClientType());
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(),
							"Invalid Client Type sent - " + generateAddressRequest.getClientType())));
		}

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
								"Client already exists with Correlation Id")));
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
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(), "Invalid client type received in request")));
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
	 * @see com.ireslab.coinclaim.service.CcApiService#retrieveBalance(com.ireslab.
	 * coinclaim.model.AccountBalanceRequest)
	 */
	@Override
	public AccountBalanceResponse retrieveBalance(AccountBalanceRequest accountBalanceRequest) {

		AccountBalanceResponse accountBalanceResponse = null;
		List<AccountDetails> accountDetailsList = new ArrayList<>();

		String clientCorrelationId = accountBalanceRequest.getClientCorrelationId();

		// Check Invalid Client Type
		ClientType clientType = null;
		try {
			clientType = ClientType.valueOf(accountBalanceRequest.getClientType());
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(), "Invalid Client Type sent - " + clientType)));
		}

		// Validating Api Request
		validateBaseApiRequest(accountBalanceRequest);

		/*
		 * COMPANY Account Balance Request
		 */
		if (clientType.equals(ClientType.COMPANY)) {

			CompanyAccount companyAccount = companyAccountRepo.findByCompanyCorrelationId(clientCorrelationId);
			if (companyAccount == null || companyAccount.getBtcAddress() == null) {

				LOG.error("Client doesn't exists for ClientCorrelationId - " + clientCorrelationId);
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.COMPANY_DOES_NOT_EXISTS.getCode(),
								"Client doesn't exists for Correlation Id : " + clientCorrelationId)));
			}

			// BTC Account balance
			accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), companyAccount.getEthAddress(),
					retrieveBitcoinBalance(companyAccount.getBtcAddress()).toString()));

			// ETH Account balance
			accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), companyAccount.getEthAddress(),
					retrieveEthereumBalance(companyAccount.getEthAddress()).toString()));

			// ERC20 Account balance
			try {
				LOG.debug("Getting account balances for ERC20 tokens for address - " + companyAccount.getEthAddress());

				List<CompanyToken> companyTokens = companyAccount.getCompanyTokens();
				companyTokens.forEach(companyToken -> {

					CLMTokenConfig clmTokenConfig = new CLMTokenConfig();
					clmTokenConfig.setTokenCode(companyToken.getTokenSymbol());
					clmTokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					clmTokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
					clmTokenConfig.setTokenDeployerPrivateKey("e");

					CoinClaimTokenContractService tokenContractService = CoinClaimTokenContractService
							.getContractServiceInstance(web3j, clmTokenConfig);

					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), companyAccount.getEthAddress(),
							tokenContractService.retrieveBalance(companyAccount.getEthAddress()).toString())
									.setTokenCode(companyToken.getTokenSymbol()));
				});

			} catch (Exception exp) {
				// TODO: Throw exception
			}
		}

		/*
		 * USER Account Balance Request
		 */
		else if (clientType.equals(ClientType.USER)) {

			UserAccount userAccount = userAccountRepo.findByUserCorrelationId(clientCorrelationId);

			if (userAccount != null && userAccount.getBtcAddress() != null) {
				LOG.error("Invalid correlation id | User already exists with user correlation id - "
						+ clientCorrelationId);
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.USER_ALREADY_EXISTS.getCode(),
								"Client already exists with Correlation Id")));
			}

			// BTC Account balance
			accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), userAccount.getBtcAddress(),
					retrieveBitcoinBalance(userAccount.getBtcAddress()).toString()));

			// ETH Account balance
			accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), userAccount.getEthAddress(),
					retrieveEthereumBalance(userAccount.getEthAddress()).toString()));
		}

		accountBalanceResponse = new AccountBalanceResponse(accountDetailsList, HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), null);
		accountBalanceResponse.setAccountDetails(accountDetailsList);

		return accountBalanceResponse;
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

		/*
		 * Validating User Account for given UserCorrelationId
		 */
		String userCorrelationId = transferTokensRequest.getUserCorrelationId();
		if (userCorrelationId == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.MISSING_OR_INVALID_USER_CORRELATION_ID.getCode(),
							"Missing or Invalid User Correlation Id : " + userCorrelationId)));
		}

		UserAccount userAccount = userAccountRepo.findByUserCorrelationId(userCorrelationId);
		if (userAccount == null || userAccount.getBtcAddress() == null) {
			LOG.error("User doesn't exists for UserCorrelationId - " + userCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.USER_DOES_NOT_EXISTS.getCode(),
							"User doesn't exists for User Correlation Id : " + userCorrelationId)));
		}

		/*
		 * Validate noOfTokens
		 */
		String noOfTokens = transferTokensRequest.getNoOfTokens();
		if (noOfTokens == null || new BigDecimal(noOfTokens).floatValue() < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_AMOUNT.getCode(), "Invalid Token Amount : " + noOfTokens)));
		}

		String tokenType = transferTokensRequest.getTokenType();
		String tokenCode = transferTokensRequest.getTokenCode();

		// Check for Token Type
		try {
			TokenType.valueOf(tokenType);
		} catch (NullPointerException | IllegalArgumentException illExp) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_TYPE.getCode(), "Invalid Token Type sent - " + tokenType)));
		}

		String clientAddress = null;
		String userAddress = null;

		// CLM TOKEN transfer request (from CoinClaim Master Account to User Account)
		if (tokenType.equals(TokenType.ERC20.name()) && tokenCode.equalsIgnoreCase(clmTokenConfig.getTokenCode())) {

			TransactionReceipt transactionReceipt = null;
			userAddress = userAccount.getEthAddress();

			BigInteger tokenQuantity = new BigDecimal(noOfTokens)
					.multiply(new BigDecimal(clmTokenConfig.getTokenDecimal())).toBigInteger();

			LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenCode + " CoinClaim's tokens To Address-  "
					+ userAddress);

			try {
				CoinClaimTokenContractService ccTokenContractService = CoinClaimTokenContractService
						.getContractServiceInstance(web3j, clmTokenConfig);

				transactionReceipt = ccTokenContractService.allocateTokens(userAddress, tokenQuantity);

				if (transactionReceipt != null) {
					LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

					if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
						throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
					}
				}
			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException("Error occurred while transferring ERC-20 tokens - " + exp);
			}
		}

		// BTC/ETH/ERC20 TOKEN transfer request (from Company's wallet to User Account)
		else {

			/*
			 * Validating Client Account for given ClientCorrelationId
			 */
			String clientCorrelationId = transferTokensRequest.getClientCorrelationId();
			validateBaseApiRequest(transferTokensRequest);

			CompanyAccount companyAccount = companyAccountRepo.findByCompanyCorrelationId(clientCorrelationId);
			if (companyAccount == null || companyAccount.getBtcAddress() == null) {
				LOG.error("Client doesn't exists for ClientCorrelationId - " + clientCorrelationId);
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.COMPANY_DOES_NOT_EXISTS.getCode(),
								"Client doesn't exists for Correlation Id : " + clientCorrelationId)));
			}

			TransactionDto transactionDto = new TransactionDto();
			transactionDto.setIndex(companyAccount.getChildIndex().intValue());

			switch (TokenType.valueOf(tokenType)) {
			case BTC:

				clientAddress = companyAccount.getBtcAddress();
				userAddress = userAccount.getBtcAddress();

				BigInteger amountInSatoshi = new BigDecimal(noOfTokens).multiply(AppConstants.BTC_DECIMAL_DIV)
						.toBigInteger();

				transactionDto.setAmount(amountInSatoshi);
				transactionDto.setFromAddress(clientAddress);
				transactionDto.setToAddress(userAddress);

				try {
					LOG.debug("Initiating transfer of '" + amountInSatoshi + "' satoshis From : '" + clientAddress
							+ "' , To : " + userAddress);

					transactionDto = bitcoinTxnService.transferTokens(transactionDto);
					LOG.debug("Bitcoins transferred successfully - " + transactionDto.getTransactionReciept());

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring bitcoins");
					throw new ApiException("Error occurred while transferring bitcoins", exp);
				}

				try {
					String balance = String
							.valueOf(new BigDecimal(bitcoinTxnService.retrieveBalance(clientAddress).getAmount())
									.divide(AppConstants.BTC_DECIMAL_DIV).doubleValue());
					LOG.debug("Updated Bitcoin balance for client is  - " + balance);

				} catch (Exception exp) {
					LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
				}
				break;

			case ETH:

				clientAddress = companyAccount.getEthAddress();
				userAddress = userAccount.getEthAddress();

				BigInteger amountInWei = new BigDecimal(noOfTokens).multiply(AppConstants.ETH_DECIMAL_DIV)
						.toBigInteger();

				transactionDto.setAmount(amountInWei);
				transactionDto.setFromAddress(clientAddress);
				transactionDto.setToAddress(userAddress);

				try {
					LOG.debug("Initiating transfer of '" + amountInWei + "' wei From : '" + clientAddress + "' , To : "
							+ userAddress);
					transactionDto = ethereumTxnService.transferTokens(transactionDto);
					LOG.debug("Ethers transferred successfully - " + transactionDto.getTransactionReciept());

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring ethers");
					throw new ApiException("Error occurred while transferring ethers", exp);
				}

				try {
					String balance = String
							.valueOf(new BigDecimal(ethereumTxnService.retrieveBalance(clientAddress).getAmount())
									.divide(AppConstants.ETH_DECIMAL_DIV).doubleValue());
					LOG.debug("Updated Ethereum balance for client is  - " + balance);

				} catch (Exception exp) {
					LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
				}
				break;

			case ERC20:

				TransactionReceipt transactionReceipt = null;

				CompanyToken companyToken = companyTokenRepo.findByTokenSymbolAndCompanyAccount_CompanyAccountId(
						tokenCode, companyAccount.getCompanyAccountId());

				if (companyToken == null) {
					throw new ApiException(HttpStatus.BAD_REQUEST,
							Arrays.asList(new Error(ResponseCode.TOKEN_DOES_NOT_EXISTS.getCode(),
									"Token doesn't exists with Token Code : " + tokenCode
											+ " , for Company with CorrelationId - " + clientCorrelationId)));
				}

				BigInteger tokenQuantity = new BigDecimal(noOfTokens)
						.multiply(new BigDecimal(companyToken.getTokenDecimals())).toBigInteger();
				userAddress = userAccount.getEthAddress();

				try {
					// Getting Company account's Private Key
					String privateKey = ethereumTxnService
							.derivePrivateKey(companyAccount.getChildIndex(), ClientType.COMPANY)
							.getEthereumAddressPrivateKey();

					LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenCode + " tokens , To : "
							+ userAddress);

					CLMTokenConfig clmTokenConfig = new CLMTokenConfig();
					clmTokenConfig.setTokenCode(companyToken.getTokenSymbol());
					clmTokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					clmTokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
					clmTokenConfig.setTokenDeployerPrivateKey(privateKey);

					CoinClaimTokenContractService ccTokenContractService = CoinClaimTokenContractService
							.getContractServiceInstance(web3j, clmTokenConfig);

					transactionReceipt = ccTokenContractService.allocateTokens(userAddress, tokenQuantity);

					if (transactionReceipt != null) {
						LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

						if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
							throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
						}
					}
				} catch (Exception exp) {
					LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));
					throw new ApiException("Error occurred while transferring ERC-20 tokens - " + exp);
				}
				break;

			default:
				break;
			}
		}

		transferTokensResponse = new TokenTransferResponse(HttpStatus.OK.value(), ResponseCode.SUCCESS.getCode(),
				"Success");

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
		CompanyAccount companyAccount = companyAccountRepo.findByCompanyCorrelationId(clientCorrelationId);

		if (companyAccount == null || companyAccount.getBtcAddress() == null) {
			LOG.error("Invalid correlation id | Company doesn't exists for client correlation id - "
					+ clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.COMPANY_DOES_NOT_EXISTS.getCode(),
							"Company doesn't exists with Correlation Id - " + clientCorrelationId)));
		}

		Integer tokenDecimal = Integer.parseInt(tokenDetailsRegistrationRequest.getTokenDecimals());

		CompanyToken companyToken = new CompanyToken();
		companyToken.setCompanyAccount(companyAccount);
		companyToken.setTokenName(tokenDetailsRegistrationRequest.getTokenName());
		companyToken.setTokenSymbol(tokenDetailsRegistrationRequest.getTokenSymbol());
		companyToken.setTokenDecimals(new BigInteger(
				StringUtils.rightPad(BigInteger.ONE.toString(), (tokenDecimal + 1), BigInteger.ZERO.toString())));
		companyToken.setTokenContractAddress(tokenDetailsRegistrationRequest.getTokenContractAddress());
		companyToken.setTokenContractBinary(tokenDetailsRegistrationRequest.getTokenContractBinary());

		// Save into database
		try {
			companyTokenRepo.save(companyToken);
			LOG.debug("Company token details persisted in database . . .");

		} catch (DataIntegrityViolationException dexp) {
			LOG.error("Token with token symbol - '" + tokenDetailsRegistrationRequest.getTokenSymbol()
					+ "' already exists for client");
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.TOKEN_ALREADY_EXISTS.getCode(), "Token with token symbol - '"
							+ tokenDetailsRegistrationRequest.getTokenSymbol() + "' already exits for client")));

		} catch (Exception exp) {
			LOG.error("Error occurred while persisting company token details in database");
			throw new ApiException("Error while persisting company account details", exp);
		}

		tokenDetailsRegistrationResponse = new TokenDetailsRegistrationResponse(HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), "Token Details Successfully saved");

		return tokenDetailsRegistrationResponse;
	}

	/**
	 * @param tokenDetailsRegistrationRequest
	 */
	private void validateTokenDetails(TokenDetailsRegistrationRequest tokenDetailsRegistrationRequest) {

		if (StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenCode())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenSymbol())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenDecimals())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenContractAddress())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenContractBinary())) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.INVALID_CLIENT_TYPE.getCode(), "Invalid Token Details")));
		}
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
			LOG.debug("Unique generated addresses:\n" + "\t Bitcoin (BTC) Address generated - "
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CcApiService#retrieveUserTokenBalance(com.
	 * ireslab.coinclaim.model.AccountBalanceRequest)
	 */
	// @Override
	// public AccountBalanceResponse retrieveUserTokenBalance(AccountBalanceRequest
	// accountBalanceRequest) {
	//
	// AccountBalanceResponse accountBalanceResponse = null;
	//
	// CoinClaimTokenContractService ccTokenContractService =
	// CoinClaimTokenContractService
	// .getContractServiceInstance(web3j, clmTokenConfig);
	//
	// BigDecimal tokenBalance = null;
	// try {
	// tokenBalance = new BigDecimal(
	// ccTokenContractService.retrieveBalance(accountBalanceRequest.getBeneficiaryAddress()))
	// .divide(new BigDecimal(clmTokenConfig.getTokenDecimal()));
	//
	// if (tokenBalance == null) {
	// throw new Exception("Cannot retrieve balance");
	// }
	//
	// LOG.debug("Token balance for beneficiary address '" +
	// accountBalanceRequest.getBeneficiaryAddress()
	// + "' is - " + tokenBalance);
	//
	// } catch (Exception exp) {
	// throw new ApiException("Error occurred while retrieving token balance", exp);
	// }
	//
	// accountBalanceResponse = new AccountBalanceResponse(HttpStatus.OK.value(),
	// ResponseCode.SUCCESS.getCode(),
	// "Success");
	// accountBalanceResponse.setAccountDetails(Arrays.asList(new
	// AccountDetails(TokenType.ERC20.name(),
	// accountBalanceRequest.getBeneficiaryAddress(), tokenBalance.toString())));
	//
	// return accountBalanceResponse;
	// }
}
