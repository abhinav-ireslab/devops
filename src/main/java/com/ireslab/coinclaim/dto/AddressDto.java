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

	private String clientType;

	private String ethereumAddressPrivateKey;

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

	/**
	 * @return the accountType
	 */
	public String getClientType() {
		return clientType;
	}

	/**
	 * @param clientType
	 *            the accountType to set
	 */
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	/**
	 * @return the privateKey
	 */
	public String getEthereumAddressPrivateKey() {
		return ethereumAddressPrivateKey;
	}

	/**
	 * @param ethereumAddressPrivateKey
	 *            the privateKey to set
	 */
	public void setEthereumAddressPrivateKey(String ethereumAddressPrivateKey) {
		this.ethereumAddressPrivateKey = ethereumAddressPrivateKey;
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
