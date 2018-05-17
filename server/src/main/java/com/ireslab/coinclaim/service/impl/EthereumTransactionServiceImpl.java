package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.properties.NodeConfigProperties;
import com.ireslab.coinclaim.service.BlockchainTransactionService;

@Service
public class EthereumTransactionServiceImpl implements BlockchainTransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(EthereumTransactionServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	@Qualifier("bitcoinTransactionServiceImpl")
	private BlockchainTransactionService bitcoinTxnService;

	@Autowired
	private NodeConfigProperties nodeConfigProperties;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#retrieveBalance(
	 * java.lang.String)
	 */
	@Override
	public TransactionDto retrieveBalance(String address) {

		LOG.debug("Calling node server to retrieve ethereum account balance for address - " + address);

		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getEthBalanceEndpoint();
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setFromAddress(address);

		transactionDto = restTemplate.postForObject(url, transactionDto, TransactionDto.class);

		return transactionDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.BlockchainTransactionService#generateAddress(
	 * java.math.BigInteger)
	 */
	@Override
	public AddressDto generateAddress(BigInteger index) {

		LOG.debug("Calling node server to retrieve addresses for index - " + index);
		return bitcoinTxnService.generateAddress(index);
	}

	@Override
	public TransactionDto transferTokens(TransactionDto transactionDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
