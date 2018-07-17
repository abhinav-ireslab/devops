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
import com.ireslab.coinclaim.utils.TokenConfig;
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

		String successMessage = null;
		TokenTransferResponse transferTokensResponse = null;

		List<Error> errors = new ArrayList<>();
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

		/**
		 * CLM TOKEN transfer request (from CoinClaim Master Account to User Account)
		 */
		if (tokenType.equals(TokenType.ERC20) && tokenSymbol.equalsIgnoreCase(clmTokenConfig.getTokenSymbol())) {

			TransactionReceipt transactionReceipt = null;
			userAddress = userAccount.getEthAddress();

			LOG.debug("Initiating transfer of " + noOfTokens + " '" + tokenSymbol + "' CoinClaim's tokens To Address-  "
					+ userAddress);
			try {
				TokenContractService clmTokenContractService = TokenContractService.getContractServiceInstance(web3j,
						clmTokenConfig);

				BigInteger clmTokenQuantity = new BigDecimal(noOfTokens)
						.multiply(new BigDecimal(clmTokenConfig.getTokenDecimal())).toBigInteger();

				transactionReceipt = clmTokenContractService.allocateTokens(userAddress, clmTokenQuantity);

				if (transactionReceipt != null) {
					LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

					if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
						throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
					}
				}

				// Retrieving updated CLM Token Balance
				try {
					BigInteger clmTokenBalance = clmTokenContractService.retrieveBalance(userAddress)
							.divide(new BigInteger(clmTokenConfig.getTokenDecimal()));

					accountDetailsList
							.add(new AccountDetails(TokenType.ERC20.name(), userAddress, clmTokenBalance.toString())
									.setTokenCode(tokenSymbol));

				} catch (Exception exp) {
					LOG.error(
							"Failed to get '" + tokenSymbol + "' token balance - " + ExceptionUtils.getStackTrace(exp));
					errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
							"Failed to get '" + tokenSymbol + "' token balance"));
				}

				successMessage = noOfTokens + " no of '" + tokenSymbol + "' CoinClaim's tokens successfully allocated";

			} catch (Exception exp) {
				LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));
				throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
						Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
								"Error occurred while transferring ERC20 tokens - " + tokenSymbol)));
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

					successMessage = noOfTokens + " Bitcoins (BTC) successfully transferred";

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring bitcoins - " + ExceptionUtils.getStackTrace(exp));
					throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
							Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
									"Error occurred while transferring bitcoins")));
				}

				// Getting updated BTC balance
				try {
					accountDetailsList.add(new AccountDetails(TokenType.BTC.name(), companyAddress,
							retrieveBitcoinBalance(companyAddress).toString()));

				} catch (Exception exp) {
					LOG.error("Failed to get BTC balance - " + ExceptionUtils.getStackTrace(exp));
					errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
							"Failed to get BTC balance"));
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

					successMessage = noOfTokens + " Ethers (ETH) successfully transferred";

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring ethers");
					throw new ApiException("Error occurred while transferring ethers", exp);
				}

				// Getting updated ETH balance
				try {
					accountDetailsList.add(new AccountDetails(TokenType.ETH.name(), companyAddress,
							retrieveEthereumBalance(companyAddress).toString()));

				} catch (Exception exp) {
					LOG.error("Failed to get ETH balance - " + ExceptionUtils.getStackTrace(exp));
					errors.add(new Error(ResponseCode.FAILED_RETRIEVING_TOKEN_BALANCE.getCode(),
							"Failed to get ETH balance"));
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

					TokenConfig erc20TokenConfig = new TokenConfig();
					erc20TokenConfig.setTokenSymbol(companyToken.getTokenSymbol());
					erc20TokenConfig.setTokenDecimal(companyToken.getTokenDecimals().toString());
					erc20TokenConfig.setTokenContractAddress(companyToken.getTokenContractAddress());
					erc20TokenConfig.setTokenContractBinary(companyToken.getTokenContractBinary());
					erc20TokenConfig.setTokenDeployerPrivateKey(privateKey);

					TokenContractService ccTokenContractService = TokenContractService.getContractServiceInstance(web3j,
							erc20TokenConfig);

					TransactionReceipt transactionReceipt = ccTokenContractService.allocateTokens(userAddress,
							tokenQuantity);

					if (transactionReceipt != null) {
						LOG.debug("Transaction Receipt - " + objectWriter.writeValueAsString(transactionReceipt));

						if (!transactionReceipt.getStatus().equalsIgnoreCase(AppConstants.TRANSACTION_STATUS_SUCCESS)) {
							throw new Exception("Transaction failed with status - " + transactionReceipt.getStatus());
						}
					}

					successMessage = noOfTokens + " no of '" + tokenSymbol + "' tokens successfully transferred";

					String erc20TokenBalance = String
							.valueOf(new BigDecimal(ccTokenContractService.retrieveBalance(companyAddress))
									.divide(new BigDecimal(erc20TokenConfig.getTokenDecimal())).doubleValue());

					// ERC20 Account Balance
					accountDetailsList.add(new AccountDetails(TokenType.ERC20.name(), companyAddress, erc20TokenBalance)
							.setTokenCode(erc20TokenConfig.getTokenSymbol()));

				} catch (Exception exp) {
					LOG.error("Error occurred while transferring ERC-20 tokens - " + ExceptionUtils.getStackTrace(exp));
					throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
							Arrays.asList(new Error(ResponseCode.TOKEN_TRANSFER_FAILED.getCode(),
									"Error occurred while transferring ERC20 tokens - " + tokenSymbol)));
				}
				break;

			default:
				throw new ApiException(HttpStatus.BAD_REQUEST,
						Arrays.asList(new Error(ResponseCode.INVALID_TOKEN_TYPE.getCode(),
								"Invalid Token Type received in request")));
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
