package com.ireslab.coinclaim.web;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.ireslab.coinclaim.model.TokenDetailsRegistrationRequest;
import com.ireslab.coinclaim.properties.ApiConfigProperties;
import com.ireslab.coinclaim.service.CommonService;
import com.ireslab.coinclaim.service.impl.CoinClaimTokenContractService;
import com.ireslab.coinclaim.utils.CLMTokenConfig;
import com.ireslab.coinclaim.utils.ClientType;

/**
 * @author iRESlab
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {

	private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

	@Autowired
	ApiConfigProperties apiConfig;

	@Autowired
	private Web3j web3j;

	@Autowired
	private CLMTokenConfig tokenConfig;

	@Autowired
	private ObjectWriter writer;

	@Autowired
	CommonService commonService;

	/**
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String testStartup() {

		LOG.info("Server is up and running . . . . " + apiConfig.getOauth2().getClientApiKey());
		return "Server is up and running . . . . ";
	}

	// @RequestMapping(value = "/test")
	// public String testMethod() {
	//
	// System.out.println("TestController.testMethod()");
	// return "success";
	// }

	@RequestMapping(value = "/test")
	public String testMethodParam(@RequestParam(name = "clientType") String clientType) {

		System.out.println("TestController.testMethodParam()");

		BigInteger index = commonService.getUniqueAddressIndex(ClientType.valueOf(clientType));

		System.out.println("Unique Address Index for clientType - " + clientType + " is - " + index);

		return "" + index;
	}

	// http://localhost:8180/cc-blockchain-api/test/allocateTokens?beneficiaryAddress=&tokenQuantity=
	@RequestMapping(value = "/allocateTokens")
	public TransactionReceipt allocateTokens(@RequestParam(value = "beneficiaryAddress") String beneficiaryAddress,
			@RequestParam(value = "tokenQuantity") String tokenQuantity) throws Exception {

		System.out.println("TestController.allocateTokens()");
		CoinClaimTokenContractService coinClaimContractService = CoinClaimTokenContractService
				.getContractServiceInstance(web3j, tokenConfig);

		BigInteger totalQuantity = new BigInteger(tokenQuantity)
				.multiply(new BigInteger(tokenConfig.getTokenDecimal()));

		TransactionReceipt transactionReceipt = coinClaimContractService.allocateTokens(beneficiaryAddress,
				totalQuantity);

		try {
			LOG.debug("Transaction Receipt - " + writer.writeValueAsString(transactionReceipt));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transactionReceipt;
	}

	/**
	 * @param beneficiaryAddress
	 * @return
	 */
	@RequestMapping(value = "/tokenBalance")
	public BigInteger tokenBalance(@RequestParam(value = "beneficiaryAddress") String beneficiaryAddress) {

		System.out.println("TestController.tokenBalance()");

		CoinClaimTokenContractService claimTokenContractService = CoinClaimTokenContractService
				.getContractServiceInstance(web3j, tokenConfig);

		BigInteger balance = claimTokenContractService.retrieveBalance(beneficiaryAddress);
		System.out.println("Balance - " + balance);

		return balance;
	}

	public static void main(String[] args) throws JsonProcessingException {

		TokenDetailsRegistrationRequest detailsRegistrationRequest = new TokenDetailsRegistrationRequest();

		detailsRegistrationRequest.setClientCorrelationId("AMMBR");
		detailsRegistrationRequest.setTokenName("COMPANY1 Token");
		detailsRegistrationRequest.setTokenSymbol("AMMBR");
		detailsRegistrationRequest.setTokenDecimals("16");
		detailsRegistrationRequest.setTokenContractAddress("0xDB95c3E6A85D3D6914fBDd9A72e2e4e45CAd9851");
		detailsRegistrationRequest.setTokenContractBinary(
				"[ { \"constant\": true, \"inputs\": [], \"name\": \"name\", \"outputs\": [ { \"name\": \"\", \"type\": \"string\", \"value\": \"\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": false, \"inputs\": [ { \"name\": \"_spender\", \"type\": \"address\" }, { \"name\": \"_value\", \"type\": \"uint256\" } ], \"name\": \"approve\", \"outputs\": [ { \"name\": \"\", \"type\": \"bool\" } ], \"payable\": false, \"stateMutability\": \"nonpayable\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"totalSupply\", \"outputs\": [ { \"name\": \"\", \"type\": \"uint256\", \"value\": \"2e+25\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": false, \"inputs\": [ { \"name\": \"_from\", \"type\": \"address\" }, { \"name\": \"_to\", \"type\": \"address\" }, { \"name\": \"_value\", \"type\": \"uint256\" } ], \"name\": \"transferFrom\", \"outputs\": [ { \"name\": \"\", \"type\": \"bool\" } ], \"payable\": false, \"stateMutability\": \"nonpayable\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"decimals\", \"outputs\": [ { \"name\": \"\", \"type\": \"uint8\", \"value\": \"0\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"cap\", \"outputs\": [ { \"name\": \"\", \"type\": \"uint256\", \"value\": \"2e+25\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": false, \"inputs\": [ { \"name\": \"_to\", \"type\": \"address\" }, { \"name\": \"_amount\", \"type\": \"uint256\" } ], \"name\": \"mint\", \"outputs\": [ { \"name\": \"\", \"type\": \"bool\" } ], \"payable\": false, \"stateMutability\": \"nonpayable\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [ { \"name\": \"_owner\", \"type\": \"address\" } ], \"name\": \"balanceOf\", \"outputs\": [ { \"name\": \"balance\", \"type\": \"uint256\", \"value\": \"0\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"owner\", \"outputs\": [ { \"name\": \"\", \"type\": \"address\", \"value\": \"0x5d1c5b90407ab50e4b428d617d33728467709d7e\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"symbol\", \"outputs\": [ { \"name\": \"\", \"type\": \"string\", \"value\": \"\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [], \"name\": \"maxMintBlock\", \"outputs\": [ { \"name\": \"\", \"type\": \"uint256\", \"value\": \"1\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"constant\": false, \"inputs\": [ { \"name\": \"_to\", \"type\": \"address\" }, { \"name\": \"_value\", \"type\": \"uint256\" } ], \"name\": \"transfer\", \"outputs\": [ { \"name\": \"\", \"type\": \"bool\" } ], \"payable\": false, \"stateMutability\": \"nonpayable\", \"type\": \"function\" }, { \"constant\": true, \"inputs\": [ { \"name\": \"_owner\", \"type\": \"address\" }, { \"name\": \"_spender\", \"type\": \"address\" } ], \"name\": \"allowance\", \"outputs\": [ { \"name\": \"\", \"type\": \"uint256\", \"value\": \"0\" } ], \"payable\": false, \"stateMutability\": \"view\", \"type\": \"function\" }, { \"inputs\": [ { \"name\": \"_name\", \"type\": \"string\", \"index\": 0, \"typeShort\": \"string\", \"bits\": \"\", \"displayName\": \"&thinsp;<span class=\\\"punctuation\\\">_</span>&thinsp;name\", \"template\": \"elements_input_string\", \"value\": \"AmmbrToken\" }, { \"name\": \"_symbol\", \"type\": \"string\", \"index\": 1, \"typeShort\": \"string\", \"bits\": \"\", \"displayName\": \"&thinsp;<span class=\\\"punctuation\\\">_</span>&thinsp;symbol\", \"template\": \"elements_input_string\", \"value\": \"AMR\" }, { \"name\": \"_decimals\", \"type\": \"uint8\", \"index\": 2, \"typeShort\": \"uint\", \"bits\": \"8\", \"displayName\": \"&thinsp;<span class=\\\"punctuation\\\">_</span>&thinsp;decimals\", \"template\": \"elements_input_uint\", \"value\": \"16\" } ], \"payable\": false, \"stateMutability\": \"nonpayable\", \"type\": \"constructor\" }, { \"anonymous\": false, \"inputs\": [ { \"indexed\": true, \"name\": \"to\", \"type\": \"address\" }, { \"indexed\": false, \"name\": \"amount\", \"type\": \"uint256\" } ], \"name\": \"Mint\", \"type\": \"event\" }, { \"anonymous\": false, \"inputs\": [ { \"indexed\": true, \"name\": \"from\", \"type\": \"address\" }, { \"indexed\": true, \"name\": \"to\", \"type\": \"address\" }, { \"indexed\": false, \"name\": \"value\", \"type\": \"uint256\" } ], \"name\": \"Transfer\", \"type\": \"event\" }, { \"anonymous\": false, \"inputs\": [ { \"indexed\": true, \"name\": \"owner\", \"type\": \"address\" }, { \"indexed\": true, \"name\": \"spender\", \"type\": \"address\" }, { \"indexed\": false, \"name\": \"value\", \"type\": \"uint256\" } ], \"name\": \"Approval\", \"type\": \"event\" } ]");

		System.out.println(new ObjectMapper().writeValueAsString(detailsRegistrationRequest));

		/*
		 * byte[] privateKey = { (byte) 0x51, (byte) 0xB5, (byte) 0x24, (byte) 0xB8,
		 * (byte) 0xD9, (byte) 0x64, (byte) 0x1C, (byte) 0xB5, (byte) 0x9B, (byte) 0x39,
		 * (byte) 0xFF, (byte) 0xD6, (byte) 0x13, (byte) 0x09, (byte) 0x59, (byte) 0x9D,
		 * (byte) 0x44, (byte) 0x3D, (byte) 0x40, (byte) 0x0F, (byte) 0xB7, (byte) 0x33,
		 * (byte) 0xCF, (byte) 0xC7, (byte) 0x2F, (byte) 0xD3, (byte) 0xE0, (byte) 0x43,
		 * (byte) 0x0D, (byte) 0x81, (byte) 0xEC, (byte) 0x08 };
		 * 
		 * 
		 * byte[] publicKey = { (byte) 0xe6, (byte) 0xe0, (byte) 0xd5, (byte) 0x45,
		 * (byte) 0x66, (byte) 0x38, (byte) 0x81, (byte) 0x69, (byte) 0xc4, (byte) 0xde,
		 * (byte) 0x60, (byte) 0x46, (byte) 0x43, (byte) 0x14, (byte) 0xe8, (byte) 0x7e,
		 * (byte) 0x58, (byte) 0x00, (byte) 0xaa, (byte) 0x09, (byte) 0x8b, (byte) 0xe5,
		 * (byte) 0xae, (byte) 0x3d, (byte) 0xce, (byte) 0x75, (byte) 0xda, (byte) 0x03,
		 * (byte) 0xf4, (byte) 0x70, (byte) 0x7f, (byte) 0x48, (byte) 0x00, (byte) 0xbe,
		 * (byte) 0x2b, (byte) 0x13, (byte) 0xca, (byte) 0x7f, (byte) 0xe1, (byte) 0x2a,
		 * (byte) 0x86, (byte) 0x57, (byte) 0xaa, (byte) 0xf3, (byte) 0x86, (byte) 0xa4,
		 * (byte) 0x8b, (byte) 0xbe, (byte) 0x69, (byte) 0x5a, (byte) 0x4b, (byte) 0x85,
		 * (byte) 0xaf, (byte) 0xaa, (byte) 0x3c, (byte) 0x33, (byte) 0x1b, (byte) 0x2b,
		 * (byte) 0x40, (byte) 0x35, (byte) 0xa5, (byte) 0xa4, (byte) 0xb7, (byte) 0x5d
		 * };
		 * 
		 * 
		 * byte[] msg = { (byte) 0xEC, (byte) 0x80, (byte) 0x85, (byte) 0x09, (byte)
		 * 0x8B, (byte) 0xCA, (byte) 0x5A, (byte) 0x00, (byte) 0x82, (byte) 0x52, (byte)
		 * 0x08, (byte) 0x94, (byte) 0x16, (byte) 0x52, (byte) 0xBC, (byte) 0x27, (byte)
		 * 0x60, (byte) 0x65, (byte) 0xE0, (byte) 0x1A, (byte) 0x96, (byte) 0xCE, (byte)
		 * 0xDF, (byte) 0x45, (byte) 0xD1, (byte) 0x49, (byte) 0x1C, (byte) 0xA6, (byte)
		 * 0x44, (byte) 0xB0, (byte) 0x09, (byte) 0x57, (byte) 0x88, (byte) 0x0D, (byte)
		 * 0xE0, (byte) 0xB6, (byte) 0xB3, (byte) 0xA7, (byte) 0x64, (byte) 0x00, (byte)
		 * 0x00, (byte) 0x80, (byte) 0x04, (byte) 0x80, (byte) 0x80 };
		 * 
		 * 
		 * byte[] msgHash = { (byte) 0x6F, (byte) 0xEA, (byte) 0x3F, (byte) 0xB2, (byte)
		 * 0x4C, (byte) 0x68, (byte) 0xFB, (byte) 0xE8, (byte) 0xB, (byte) 0xBC, (byte)
		 * 0x5D, (byte) 0xB8, (byte) 0x5E, (byte) 0x1B, (byte) 0x98, (byte) 0x20, (byte)
		 * 0x2F, (byte) 0x75, (byte) 0x2F, (byte) 0x92, (byte) 0x5A, (byte) 0x1F, (byte)
		 * 0xF1, (byte) 0xA6, (byte) 0x16, (byte) 0xF, (byte) 0x48, (byte) 0x62, (byte)
		 * 0xE2, (byte) 0x3B, (byte) 0xB7, (byte) 0x4A };
		 * 
		 * 
		 * System.out.
		 * println("*********************************** Direct ECDSA **********************************"
		 * );
		 * 
		 * // System.out.println("Msg Hash - " + Hex.toHexString(msgHash)); //
		 * System.out.println("Private Key (Hex) - " + Hex.toHexString(privateKey));
		 * System.out.println("Private Key (Big Integer) - " + new
		 * BigInteger(privateKey));
		 * 
		 * // ECKeyPair ecKeyPair = new ECKeyPair(new BigInteger(privateKey), new //
		 * BigInteger(publicKey)); // ECDSASignature ecdsaSignature =
		 * ecKeyPair.sign(msgHash);
		 * 
		 * // System.out.println("R - " +
		 * Hex.toHexString(ecdsaSignature.r.toByteArray())); //
		 * System.out.println("S - " + Hex.toHexString(ecdsaSignature.s.toByteArray()));
		 * 
		 * System.out
		 * .println("\n\n*********************************** Signing + ECDSA **********************************"
		 * );
		 * 
		 * 
		 * SignatureData signatureData = Sign.signMessage(msg, ecKeyPair);
		 * System.out.println(Hex.toHexString(signatureData.getR()));
		 * System.out.println(Hex.toHexString(signatureData.getS()));
		 * 
		 * 
		 * // SignatureData signatureData2 = Sign.signMessage(msg, ecKeyPair);
		 * 
		 * 
		 * byte[] messageHash = Hash.sha3(msg); System.out.println("Msg Hash - " +
		 * Hex.toHexString(messageHash)); System.out.println("Private Key (Hex) - " +
		 * Hex.toHexString(privateKey)); ECDSASignature signatureData =
		 * ecKeyPair.sign(messageHash);
		 * 
		 * System.out.println("R - " + Hex.toHexString(signatureData.r.toByteArray()));
		 * System.out.println("S - " + Hex.toHexString(signatureData.s.toByteArray()));
		 * 
		 */}
}
