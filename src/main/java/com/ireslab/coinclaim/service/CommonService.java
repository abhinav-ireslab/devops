package com.ireslab.coinclaim.service;

import java.math.BigInteger;

import com.ireslab.coinclaim.utils.ClientType;

/**
 * @author iRESlab
 *
 */
public interface CommonService {

	/**
	 * @param clientType
	 * @return
	 */
	public BigInteger getUniqueAddressIndex(ClientType clientType);
}
