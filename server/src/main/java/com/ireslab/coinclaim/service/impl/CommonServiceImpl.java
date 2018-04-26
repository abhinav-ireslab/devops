package com.ireslab.coinclaim.service.impl;

import java.math.BigInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ireslab.coinclaim.entity.UniqueIndex;
import com.ireslab.coinclaim.repository.UniqueAddressRepo;
import com.ireslab.coinclaim.service.CommonService;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ireslab.coinclaim.service.CommonService#getUniqueAddressIndex()
	 */
	@Override
	public BigInteger getUniqueAddressIndex() {
		BigInteger index = null;

		lock.lock();
		LOG.info("Reading index after lock");

		UniqueIndex lastIndex = uniqueAddressRepo.findOne(1L);
		LOG.debug("Last Index - " + lastIndex);

		if (lastIndex == null) {
			LOG.debug("Last index is null . . . .");

			lastIndex = new UniqueIndex();
			lastIndex.setId(1L);
			lastIndex.setUniqueIndex(BigInteger.ZERO);
		}

		index = lastIndex.getUniqueIndex().add(BigInteger.ONE);
		lastIndex.setUniqueIndex(index);

		lastIndex = uniqueAddressRepo.save(lastIndex);
		lock.unlock();

		return index;
	}
}
