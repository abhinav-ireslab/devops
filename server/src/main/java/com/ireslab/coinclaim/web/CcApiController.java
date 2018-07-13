package com.ireslab.coinclaim.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.coinclaim.model.AccountBalanceRequest;
import com.ireslab.coinclaim.model.AccountBalanceResponse;
import com.ireslab.coinclaim.model.GenerateAddressRequest;
import com.ireslab.coinclaim.model.GenerateAddressResponse;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationRequest;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationResponse;
import com.ireslab.coinclaim.model.TokenTransferRequest;
import com.ireslab.coinclaim.model.TokenTransferResponse;
import com.ireslab.coinclaim.service.CcApiService;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping(value = "/v1/*", produces = MediaType.APPLICATION_JSON_VALUE)
public class CcApiController {

	private static final Logger LOG = LoggerFactory.getLogger(CcApiController.class);

	@Autowired
	private ObjectWriter objectWriter;

	@Autowired
	private CcApiService ccApiService;

	/**
	 * POST http://localhost:8180/cc-blockchain-api/address
	 * 
	 * @param generateAddressRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "address", method = RequestMethod.POST)
	public ResponseEntity<GenerateAddressResponse> generateCompanyAddress(
			@RequestBody GenerateAddressRequest generateAddressRequest) throws JsonProcessingException {

		GenerateAddressResponse generateAddressResponse = null;
		LOG.info(
				"Request recieved for Address Generation - " + objectWriter.writeValueAsString(generateAddressRequest));

		generateAddressResponse = ccApiService.generateAddress(generateAddressRequest);
		LOG.debug("Response sent for Address Generation - " + objectWriter.writeValueAsString(generateAddressResponse));

		return new ResponseEntity<>(generateAddressResponse, HttpStatus.OK);
	}

	/**
	 * POST http://localhost:8180/cc-blockchain-api/balance
	 * 
	 * @param accountBalanceRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "balance", method = RequestMethod.POST)
	public ResponseEntity<AccountBalanceResponse> accountBalance(
			@RequestBody AccountBalanceRequest accountBalanceRequest) throws JsonProcessingException {

		AccountBalanceResponse accountBalanceResponse = null;
		LOG.info("Request recieved for Account Balance - " + objectWriter.writeValueAsString(accountBalanceRequest));

		accountBalanceResponse = ccApiService.retrieveBalance(accountBalanceRequest);
		LOG.info("Response sent for Account Balance - " + objectWriter.writeValueAsString(accountBalanceResponse));

		return new ResponseEntity<>(accountBalanceResponse, HttpStatus.OK);
	}

	/**
	 * @param tokenDetailsRegistrationReq
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "registerTokenDetails", method = RequestMethod.POST)
	public ResponseEntity<TokenDetailsRegistrationResponse> registerTokenContract(
			@RequestBody TokenDetailsRegistrationRequest tokenDetailsRegistrationReq) throws JsonProcessingException {

		TokenDetailsRegistrationResponse tokenDetailsRegistrationResponse = null;
		LOG.info("Request recieved for Account Balance - "
				+ objectWriter.writeValueAsString(tokenDetailsRegistrationReq));

		tokenDetailsRegistrationResponse = ccApiService.saveTokenDetails(tokenDetailsRegistrationReq);
		LOG.info("Response sent for Account Balance - "
				+ objectWriter.writeValueAsString(tokenDetailsRegistrationResponse));

		return new ResponseEntity<>(tokenDetailsRegistrationResponse, HttpStatus.OK);
	}

	/**
	 * POST http://localhost:8180/cc-blockchain-api/transfer
	 * 
	 * @param accountBalanceRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping(value = "transfer", method = RequestMethod.POST)
	public ResponseEntity<TokenTransferResponse> transferTokens(@RequestBody TokenTransferRequest transferTokensRequest)
			throws JsonProcessingException {

		TokenTransferResponse transferTokensResponse = null;
		LOG.info("Request recieved for Account Balance - " + objectWriter.writeValueAsString(transferTokensRequest));

		transferTokensResponse = ccApiService.transferTokens(transferTokensRequest);
		LOG.info("Request recieved for Account Balance - " + objectWriter.writeValueAsString(transferTokensResponse));

		return new ResponseEntity<>(transferTokensResponse, HttpStatus.OK);
	}

	/**
	 * @param accountBalanceRequest
	 * @return
	 * @throws JsonProcessingException
	 */
	// @RequestMapping(value = "user/tokenBalance", method = RequestMethod.POST)
	// public ResponseEntity<AccountBalanceResponse> userTokenBalance(
	// @RequestBody AccountBalanceRequest accountBalanceRequest) throws
	// JsonProcessingException {
	//
	// AccountBalanceResponse accountBalanceResponse = null;
	// LOG.info("Request recieved for Token Balance - " +
	// objectWriter.writeValueAsString(accountBalanceRequest));
	//
	// accountBalanceResponse =
	// ccApiService.retrieveUserTokenBalance(accountBalanceRequest);
	// LOG.info("Response sent for Account Balance - " +
	// objectWriter.writeValueAsString(accountBalanceResponse));
	//
	// return new ResponseEntity<>(accountBalanceResponse, HttpStatus.OK);
	// }
}
