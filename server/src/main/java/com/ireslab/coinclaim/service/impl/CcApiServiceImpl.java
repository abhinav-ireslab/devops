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

		String successMessage = null;
		TokenTransferResponse transferTokensResponse = null;
		List<AccountDetails> accountDetailsList = new ArrayList<>();

		TokenType tokenType = validateTokenType(transferTokensRequest.getTokenType());
		String tokenSymbol = transferTokensRequest.getTokenSymbol();

		// Validating User Account for given UserCorrelationId
		UserAccount userAccount = getUserAccount(transferTokensRequest.getUserCorrelationId());

		// Validate noOfTokens
		String noOfTokens = transferTokensRequest.getNoOfTokens();
		if (noOfTokens == null || new BigDecimal(noOfTokens).floatValue() < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, Arrays.asList(
					new Error(ResponseCode.INVALID_TOKEN_AMOUNT.getCode(), "Invalid Token Amount : " + noOfTokens)));
		}

		String companyAddress = null;
		String userAddress = null;

		/*
		 * CLM TOKEN transfer request (from CoinClaim Master Account to User Account)
		 */
		if (tokenType.equals(TokenType.ERC20) && tokenSymbol.equalsIgnoreCase(clmTokenConfig.getTokenSymbol())) {

			TransactionReceipt transactionReceipt = null;
			userAddress = userAccount.getEthAddress();

			BigInteger tokenQuantity = new BigDecimal(noOfTokens)
					.multiply(new BigDecimal(clmTokenConfig.getTokenDecimal())).toBigInteger();

			LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenSymbol + "' CoinClaim's tokens To Address-  "
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
				successMessage = noOfTokens + " no of '" + tokenSymbol + "' CoinClaim's tokens successfully allocated";

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
						Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
								"Error occurred while transferring ERC20 tokens")));
			}
		}

		/*
		 * BTC/ETH/ERC20 TOKEN transfer request (from Company's wallet to User Account)
		 */
		else {
			validateBaseApiRequest(transferTokensRequest);

			// Validating Client Account for given ClientCorrelationId
			CompanyAccount companyAccount = getCompanyAccount(transferTokensRequest.getClientCorrelationId());

			TransactionDto transactionDto = new TransactionDto();
			transactionDto.setIndex(companyAccount.getChildIndex().intValue());

			switch (tokenType) {
			case BTC:
				companyAddress = companyAccount.getBtcAddress();
				userAddress = userAccount.getBtcAddress();

				BigInteger amountInSatoshi = new BigDecimal(noOfTokens).multiply(AppConstants.BTC_DECIMAL_DIV)
						.toBigInteger();

				transactionDto.setAmount(amountInSatoshi);
				transactionDto.setFromAddress(companyAddress);
				transactionDto.setToAddress(userAddress);

				try {
					LOG.debug("Initiating transfer of '" + amountInSatoshi + "' satoshis From : '" + companyAddress
							+ "' , To : " + userAddress);

					transactionDto = bitcoinTxnService.transferTokens(transactionDto);
					LOG.debug("Response from node server - " + transactionDto.toString());

					LOG.debug("Bitcoins transferred successfully - " + transactionDto.getTransactionReciept());

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring bitcoins");
					throw new ApiException("Error occurred while transferring bitcoins", exp);
				}

				try {
					String balance = String
							.valueOf(new BigDecimal(bitcoinTxnService.retrieveBalance(companyAddress).getAmount())
									.divide(AppConstants.BTC_DECIMAL_DIV).doubleValue());

					LOG.debug("Updated Bitcoin balance for client is  - " + balance);
					accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), companyAddress, balance));

				} catch (Exception exp) {
					LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
				}
				break;

			case ETH:
				companyAddress = companyAccount.getEthAddress();
				userAddress = userAccount.getEthAddress();

				BigInteger amountInWei = new BigDecimal(noOfTokens).multiply(AppConstants.ETH_DECIMAL_DIV)
						.toBigInteger();

				transactionDto.setAmount(amountInWei);
				transactionDto.setFromAddress(companyAddress);
				transactionDto.setToAddress(userAddress);

				try {
					LOG.debug("Initiating transfer of '" + amountInWei + "' wei From : '" + companyAddress + "' , To : "
							+ userAddress);
					transactionDto = ethereumTxnService.transferTokens(transactionDto);
					LOG.debug("Ethers transferred successfully - " + transactionDto.getTransactionReciept());

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring ethers");
					throw new ApiException("Error occurred while transferring ethers", exp);
				}

				try {
					String balance = String
							.valueOf(new BigDecimal(ethereumTxnService.retrieveBalance(companyAddress).getAmount())
									.divide(AppConstants.ETH_DECIMAL_DIV).doubleValue());

					LOG.debug("Updated Ethereum balance for client is  - " + balance);
					accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), companyAddress, balance));

				} catch (Exception exp) {
					LOG.error("Error occurred while getting updated balance" + ExceptionUtils.getStackTrace(exp));
				}
				break;

			case ERC20:
				companyAddress = companyAccount.getEthAddress();
				userAddress = userAccount.getEthAddress();

				CompanyToken companyToken = companyTokenRepo.findByTokenSymbolAndCompanyAccount_CompanyAccountId(
						tokenSymbol, companyAccount.getCompanyAccountId());

				if (companyToken == null) {
					throw new ApiException(HttpStatus.BAD_REQUEST,
							Arrays.asList(new Error(ResponseCode.TOKEN_DOES_NOT_EXISTS.getCode(),
									"Token doesn't exists with Token Code : " + tokenSymbol
											+ " , for Company with CorrelationId - "
											+ transferTokensRequest.getClientCorrelationId())));
				}

				BigInteger tokenQuantity = new BigDecimal(noOfTokens)
						.multiply(new BigDecimal(companyToken.getTokenDecimals())).toBigInteger();

				try {
					// Getting Company account's Private Key
					String privateKey = ethereumTxnService
							.derivePrivateKey(companyAccount.getChildIndex(), ClientType.COMPANY)
							.getEthereumAddressPrivateKey();

					LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenSymbol + " tokens , To : "
							+ userAddress);

					CLMTokenConfig clmTokenConfig = new CLMTokenConfig();
					clmTokenConfig.setTokenSymbol(companyToken.getTokenSymbol());
					clmTokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					clmTokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
					clmTokenConfig.setTokenDeployerPrivateKey(privateKey);

					CoinClaimTokenContractService ccTokenContractService = CoinClaimTokenContractService
							.getContractServiceInstance(web3j, clmTokenConfig);

					TransactionReceipt transactionReceipt = ccTokenContractService.allocateTokens(userAddress,
							tokenQuantity);

					if (transactionReceipt != null) {
						LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

						if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
							throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
						}
					}

					// ERC20 Account Balance
					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), companyAddress,
							ccTokenContractService.retrieveBalance(companyAddress).toString())
									.setTokenCode(clmTokenConfig.getTokenSymbol()));

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
				successMessage);
		transferTokensResponse.setAccountDetails(accountDetailsList);

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
					+ "' already exists for client with correlationId - " + clientCorrelationId);
			throw new ApiException(HttpStatus.BAD_REQUEST,
					Arrays.asList(new Error(ResponseCode.TOKEN_ALREADY_EXISTS.getCode(),
							"Token with token symbol '" + tokenDetailsRegistrationRequest.getTokenSymbol()
									+ "' already exits for Client Correlation Id - " + clientCorrelationId)));

		} catch (Exception exp) {
			LOG.error("Error occurred while persisting company token details in database");
			throw new ApiException("Error while persisting company account details", exp);
		}

		tokenDetailsRegistrationResponse = new TokenDetailsRegistrationResponse(HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), "Token Details for token '"
						+ tokenDetailsRegistrationRequest.getTokenSymbol() + "' successfully saved");

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
			accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), companyAccount.getBtcAddress(),
					retrieveBitcoinBalance(companyAccount.getBtcAddress()).toString()));

			// ETH Account balance
			accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), ethereumAddress,
					retrieveEthereumBalance(ethereumAddress).toString()));

			// ERC20 Account balance
			LOG.debug("Getting account balances for ERC20 tokens for address - " + ethereumAddress);

			List<CompanyToken> companyTokens = companyAccount.getCompanyTokens();
			companyTokens.forEach(companyToken -> {

				CLMTokenConfig clmTokenConfig = new CLMTokenConfig();
				clmTokenConfig.setTokenSymbol(companyToken.getTokenSymbol());
				clmTokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
				clmTokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
				clmTokenConfig.setTokenDeployerPrivateKey("e");

				CoinClaimTokenContractService tokenContractService = CoinClaimTokenContractService
						.getContractServiceInstance(web3j, clmTokenConfig);

				String balance = String.valueOf(new BigDecimal(tokenContractService.retrieveBalance(ethereumAddress))
						.divide(new BigDecimal(companyToken.getTokenDecimals())).doubleValue());

				accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress, balance.toString())
						.setTokenCode(companyToken.getTokenSymbol()));
			});
		}

		/*
		 * USER Account Balance Request
		 */
		else if (clientType.equals(ClientType.USER)) {
			UserAccount userAccount = getUserAccount(clientCorrelationId);

			ethereumAddress = userAccount.getEthAddress();

			// BTC Account balance
			accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), userAccount.getBtcAddress(),
					retrieveBitcoinBalance(userAccount.getBtcAddress()).toString()));

			// ETH Account balance
			accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), ethereumAddress,
					retrieveEthereumBalance(ethereumAddress).toString()));

			try {
				// ERC20-CLM Account balance
				CoinClaimTokenContractService clmContractService = CoinClaimTokenContractService
						.getContractServiceInstance(web3j, clmTokenConfig);

				BigInteger tokenBalance = clmContractService.retrieveBalance(ethereumAddress)
						.divide(new BigInteger(clmTokenConfig.getTokenDecimal()));

				accountDetailsList
						.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress, tokenBalance.toString())
								.setTokenCode(clmTokenConfig.getTokenSymbol()));

			} catch (Exception exp) {
				LOG.error("Failed to get '" + clmTokenConfig.getTokenSymbol() + "' token balance - "
						+ ExceptionUtils.getStackTrace(exp));
				errors.add(new Error(ResponseCode.FAILED_RETRIEVING_ERC20_TOKEN_BALANCE.getCode(),
						"Failed to get '" + clmTokenConfig.getTokenSymbol() + "' token balance"));
			}

			// ERC-20 Account Balance
			companyTokenRepo.findAll().forEach(companyToken -> {

				CLMTokenConfig clmTokenConfig = new CLMTokenConfig();
				clmTokenConfig.setTokenSymbol(companyToken.getTokenSymbol());
				clmTokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
				clmTokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
				clmTokenConfig.setTokenDeployerPrivateKey("e");

				CoinClaimTokenContractService tokenContractService = CoinClaimTokenContractService
						.getContractServiceInstance(web3j, clmTokenConfig);

				String balance = String.valueOf(new BigDecimal(tokenContractService.retrieveBalance(ethereumAddress))
						.divide(new BigDecimal(companyToken.getTokenDecimals())).doubleValue());

				accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), ethereumAddress, balance.toString())
						.setTokenCode(companyToken.getTokenSymbol()));
			});
		}

		accountBalanceResponse = new AccountBalanceResponse(accountDetailsList, HttpStatus.OK.value(),
				ResponseCode.SUCCESS.getCode(), errors);
		accountBalanceResponse.setAccountDetails(accountDetailsList);

		return accountBalanceResponse;
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

		if (StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenSymbol())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenSymbol())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenDecimals())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenContractAddress())
				|| StringUtils.isBlank(tokenDetailsRegistrationRequest.getTokenContractBinary())) {
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
