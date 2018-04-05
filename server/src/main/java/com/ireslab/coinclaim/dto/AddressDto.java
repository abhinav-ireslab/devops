package com.ireslab.coinclaim.dto;

import java.math.BigInteger;

/**
 * @author iRESlab
 *
 */
/**
 * @author iRESlab
 *
 */
public class AddressDto {

	private BigInteger index;

	private String uniqueAddress;

	/**
	 * @return the uniqueBitcoinAddress
	 */
	public String getUniqueBitcoinAddress() {
		return uniqueAddress;
	}

	/**
	 * @param uniqueBitcoinAddress
	 *            the uniqueBitcoinAddress to set
	 */
	public void setUniqueBitcoinAddress(String uniqueBitcoinAddress) {
		this.uniqueAddress = uniqueBitcoinAddress;
	}

	/**
	 * @return the index
	 */
	public BigInteger getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(BigInteger index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AddressDto [index=" + index + ", uniqueAddress=" + uniqueAddress + "]";
	}
}
