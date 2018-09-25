package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TokenDetailsDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.properties.NodeConfigProperties;
import com.ireslab.coinclaim.service.BlockchainTransactionService;
import com.ireslab.coinclaim.utils.ClientType;

/**
 * @author iRESlab
 *
 */
@Service
public class BitcoinTransactionServiceImpl implements BlockchainTransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(BitcoinTransactionServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier("ethereumTransactionServiceImpl")
	private EthereumTransactionServiceImpl ethereumTxnService;

	@Autowired
	private NodeConfigProperties nodeConfigProperties;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#generateAddress(
	 * java.math.BigInteger)
	 */
	@Override
	public AddressDto generateAddress(BigInteger index, ClientType clientType) {

		LOG.debug("Calling node server to generate unique bitcoin address for index - " + index);

		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getAddressGenerationEndpoint();
		AddressDto addressDto = new AddressDto();
		addressDto.setIndex(index);
		addressDto.setClientType(clientType.name());

		addressDto = restTemplate.postForObject(url, addressDto, AddressDto.class);
		return addressDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#retrieveBalance(
	 * java.lang.String)
	 */
	@Override
	public TransactionDto retrieveBalance(String address) {

		LOG.debug("Calling node server to retrieve bitcoin account balance for address - " + address);

		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getBtcBalanceEndpoint();
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setFromAddress(address);

		transactionDto = restTemplate.postForObject(url, transactionDto, TransactionDto.class);
		return transactionDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#transferTokens(com
	 * .ireslab.coinclaim.dto.TransactionDto)
	 */
	@Override
	public TransactionDto transferTokens(TransactionDto transactionDto) {

		final String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getBtcTransferEndpoint();
		transactionDto = restTemplate.postForObject(url, transactionDto, TransactionDto.class);

		// TODO Auto-generated method stub
		return transactionDto;
	}

	/**
	 * @param index
	 * @param clientType
	 */
	public AddressDto derivePrivateKey(BigInteger index, ClientType clientType) {

		LOG.debug("Calling node server to derive private key for address with index - " + index);

		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getPrivateKeyDerivationEndpoint();
		AddressDto addressDto = new AddressDto();
		addressDto.setIndex(index);
		addressDto.setClientType(clientType.name());

		addressDto = restTemplate.postForObject(url, addressDto, AddressDto.class);
		return addressDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#checkTokenDetails(
	 * java.lang.String)
	 */
	@Override
	public TokenDetailsDto checkTokenDetails(String tokenContractAddress) {
		return ethereumTxnService.checkTokenDetails(tokenContractAddress);
	}
}
