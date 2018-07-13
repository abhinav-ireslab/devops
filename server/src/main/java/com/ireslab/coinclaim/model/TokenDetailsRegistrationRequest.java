package com.ireslab.coinclaim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author iRESlab
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetailsRegistrationRequest extends BaseApiRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6201281112504555641L;

	private String tokenName;
	private String tokenSymbol;
	private String tokenDecimals;
	private String tokenContractAddress;
	private String tokenContractBinary;

	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName
	 *            the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return the tokenSymbol
	 */
	public String getTokenSymbol() {
		return tokenSymbol;
	}

	/**
	 * @param tokenSymbol
	 *            the tokenSymbol to set
	 */
	public void setTokenSymbol(String tokenSymbol) {
		this.tokenSymbol = tokenSymbol;
	}

	/**
	 * @return the tokenDecimals
	 */
	public String getTokenDecimals() {
		return tokenDecimals;
	}

	/**
	 * @param tokenDecimals
	 *            the tokenDecimals to set
	 */
	public void setTokenDecimals(String tokenDecimals) {
		this.tokenDecimals = tokenDecimals;
	}

	/**
	 * @return the tokenContractAddress
	 */
	public String getTokenContractAddress() {
		return tokenContractAddress;
	}

	/**
	 * @param tokenContractAddress
	 *            the tokenContractAddress to set
	 */
	public void setTokenContractAddress(String tokenContractAddress) {
		this.tokenContractAddress = tokenContractAddress;
	}

	/**
	 * @return the tokenContractBinary
	 */
	public String getTokenContractBinary() {
		return tokenContractBinary;
	}

	/**
	 * @param tokenContractBinary
	 *            the tokenContractBinary to set
	 */
	public void setTokenContractBinary(String tokenContractBinary) {
		this.tokenContractBinary = tokenContractBinary;
	}
}
