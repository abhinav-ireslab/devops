package com.ireslab.coinclaim.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author iRESlab
 *
 */
public interface AppConstants {

	public BigDecimal BTC_DECIMAL_DIV = new BigDecimal("100000000");

	public BigDecimal ETH_DECIMAL_DIV = new BigDecimal("1000000000000000000");
	
	public String TRANSACTION_STATUS_SUCCESS = "0x1";
	
	public BigInteger GWEI_TO_WEI = new BigInteger("1000000000");
	
	public BigInteger GAS_LIMIT = new BigInteger("21000");
	
	public String INTRINSIC_GAS_TOO_LOW = new String("intrinsic gas too low");
	
	public String EXCEEDS_BLOCK_GAS_LIMIT = new String("exceeds block gas limit");
	
	public Integer INSUFFICIENT_FUNDS = new Integer("111");
}
