package com.ireslab.coinclaim.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author iRESlab
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenTransferRequest extends BaseApiRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 483641603937676982L;

	private String userCorrelationId;

	private String noOfTokens;

	@JsonIgnore(value = true)
	private String senderAddress;

	@JsonIgnore(value = true)
	private BigInteger senderAddressIndex;

	private String beneficiaryAddress;
	
	private String gasPrice;
	
	private String gasLimit;

	/**
	 * @return the userCorrelationId
	 */
	public String getUserCorrelationId() {
		return userCorrelationId;
	}

	/**
	 * @param userCorrelationId
	 *            the userCorrelationId to set
	 */
	public void setUserCorrelationId(String userCorrelationId) {
		this.userCorrelationId = userCorrelationId;
	}

	/**
	 * @return the noOfTokens
	 */
	public String getNoOfTokens() {
		return noOfTokens;
	}

	/**
	 * @param noOfTokens
	 *            the noOfTokens to set
	 */
	public void setNoOfTokens(String noOfTokens) {
		this.noOfTokens = noOfTokens;
	}

	/**
	 * @return the senderAddress
	 */
	public String getSenderAddress() {
		return senderAddress;
	}

	/**
	 * @param senderAddress
	 *            the senderAddress to set
	 */
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	/**
	 * @return the senderAddressIndex
	 */
	public BigInteger getSenderAddressIndex() {
		return senderAddressIndex;
	}

	/**
	 * @param senderAddressIndex
	 *            the senderAddressIndex to set
	 */
	public void setSenderAddressIndex(BigInteger senderAddressIndex) {
		this.senderAddressIndex = senderAddressIndex;
	}

	/**
	 * @return the beneficiaryAddress
	 */
	public String getBeneficiaryAddress() {
		return beneficiaryAddress;
	}

	/**
	 * @param beneficiaryAddress
	 *            the beneficiaryAddress to set
	 */
	public void setBeneficiaryAddress(String beneficiaryAddress) {
		this.beneficiaryAddress = beneficiaryAddress;
	}

	/**
	 * @return gasPrice
	 */
	public String getGasPrice() {
		return gasPrice;
	}

	/**
	 * @param gasPrice
	 * 			the gasPrice to set
	 */
	public void setGasPrice(String gasPrice) {
		this.gasPrice = gasPrice;
	}

	/**
	 * @return gasLimit
	 */
	public String getGasLimit() {
		return gasLimit;
	}

	/**
	 * @param gasLimit
	 * 			the gasLimit to set
	 */
	public void setGasLimit(String gasLimit) {
		this.gasLimit = gasLimit;
	}
}
