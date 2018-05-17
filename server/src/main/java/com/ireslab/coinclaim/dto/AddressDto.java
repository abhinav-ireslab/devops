package com.ireslab.coinclaim.dto;

import java.math.BigInteger;

/**
 * @author iRESlab
 *
 */
public class AddressDto {

	private BigInteger index;

	private String uniqueBitcoinAddress;

	private String uniqueEthereumAddress;

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

	/**
	 * @return the uniqueBitcoinAddress
	 */
	public String getUniqueBitcoinAddress() {
		return uniqueBitcoinAddress;
	}

	/**
	 * @param uniqueBitcoinAddress
	 *            the uniqueBitcoinAddress to set
	 */
	public void setUniqueBitcoinAddress(String uniqueBitcoinAddress) {
		this.uniqueBitcoinAddress = uniqueBitcoinAddress;
	}

	/**
	 * @return the uniqueEthereumAddress
	 */
	public String getUniqueEthereumAddress() {
		return uniqueEthereumAddress;
	}

	/**
	 * @param uniqueEthereumAddress
	 *            the uniqueEthereumAddress to set
	 */
	public void setUniqueEthereumAddress(String uniqueEthereumAddress) {
		this.uniqueEthereumAddress = uniqueEthereumAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AddressDto [index=" + index + ", uniqueAddress=" + uniqueBitcoinAddress + "]";
	}
}
