package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ireslab.coinclaim.dto.TokenDetailsDto;
import com.ireslab.coinclaim.entity.UniqueIndex;
import com.ireslab.coinclaim.properties.NodeConfigProperties;
import com.ireslab.coinclaim.repository.UniqueAddressRepo;
import com.ireslab.coinclaim.service.CommonService;
import com.ireslab.coinclaim.utils.ClientType;

/**
 * @author iRESlab
 *
 */
@Service
public class CommonServiceImpl implements CommonService {

	private static final Logger LOG = LoggerFactory.getLogger(CommonServiceImpl.class);
	private Lock lock = new ReentrantLock();

	@Autowired
	private UniqueAddressRepo uniqueAddressRepo;
	
	@Autowired
	private NodeConfigProperties nodeConfigProperties;
	
	@Autowired
	RestTemplate restTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ireslab.coinclaim.service.CommonService#getUniqueAddressIndex(com.ireslab
	 * .coinclaim.utils.ClientType)
	 */
	@Override
	public BigInteger getUniqueAddressIndex(ClientType clientType) {
		BigInteger index = null;

		lock.lock();
		LOG.info("Reading index after lock");

		UniqueIndex lastIndex = uniqueAddressRepo.findOne(1L);
		LOG.debug("Last Index - " + lastIndex);

		if (lastIndex == null) {
			LOG.debug("Last index is null . . . .");

			lastIndex = new UniqueIndex();
			lastIndex.setId(1L);

			lastIndex.setUniqueCompanyIndex(BigInteger.ZERO);
			lastIndex.setUniqueUserIndex(BigInteger.ZERO);
		}

		if (clientType.equals(ClientType.COMPANY)) {
			index = lastIndex.getUniqueCompanyIndex().add(BigInteger.ONE);
			lastIndex.setUniqueCompanyIndex(index);

		} else if (clientType.equals(ClientType.USER)) {
			index = lastIndex.getUniqueUserIndex().add(BigInteger.ONE);
			lastIndex.setUniqueUserIndex(index);
		}

		lastIndex = uniqueAddressRepo.save(lastIndex);
		lock.unlock();

		return index;
	}
	
	@Override
	public TokenDetailsDto checkTokenDetails(String tokenContractAddress) {
		LOG.debug("Calling node server to check token details using Contract Adrress - "+tokenContractAddress);
		String url = nodeConfigProperties.getBaseUrl() + nodeConfigProperties.getCheckTokenDetailsEndpoint();
		TokenDetailsDto tokenDetailsDto = new TokenDetailsDto();
		tokenDetailsDto.setTokenContractAddress(tokenContractAddress);

		tokenDetailsDto = restTemplate.postForObject(url, tokenDetailsDto, TokenDetailsDto.class);

		return tokenDetailsDto;
	}
}
