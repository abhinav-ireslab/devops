package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ireslab.coinclaim.dto.AddressDto;
import com.ireslab.coinclaim.dto.TransactionDto;
import com.ireslab.coinclaim.properties.NodeConfigProperties;
import com.ireslab.coinclaim.service.BlockchainTransactionService;

/**
 * @author iRESlab
 *
 */
@Service
public class BitcoinTransactionServiceImpl implements BlockchainTransactionService {

	@Autowired
	private RestTemplate restTemplate;

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
	public AddressDto generateAddress(BigInteger index) {

		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getAddressGenerationEndpoint();
		AddressDto addressDto = new AddressDto();
		addressDto.setIndex(index);

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

		TransactionDto transaction = new TransactionDto();
		transaction.setAmount(transactionDto.getAmount());
		transaction.setFromAddress(transactionDto.getFromAddress());
		transaction.setIndex(transactionDto.getIndex());

		transaction = restTemplate.postForObject(url, transaction, TransactionDto.class);

		// TODO Auto-generated method stub
		return null;
	}

}
