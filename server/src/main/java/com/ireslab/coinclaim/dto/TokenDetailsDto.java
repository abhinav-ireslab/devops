package com.ireslab.coinclaim.dto;

/**
 * @author iRESlab
 *
 */
public class TokenDetailsDto extends NodeResponse {

	private String tokenContractAddress;
	private Object contractABI;
	private String tokenName;
	private String tokenSymbol;
	private String tokenDecimal;

	public String getTokenContractAddress() {
		return tokenContractAddress;
	}

	public void setTokenContractAddress(String tokenContractAddress) {
		this.tokenContractAddress = tokenContractAddress;
	}

	public Object getContractABI() {
		return contractABI;
	}

	public void setContractABI(Object contractABI) {
		this.contractABI = contractABI;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public String getTokenSymbol() {
		return tokenSymbol;
	}

	public void setTokenSymbol(String tokenSymbol) {
		this.tokenSymbol = tokenSymbol;
	}

	public String getTokenDecimal() {
		return tokenDecimal;
	}

	public void setTokenDecimal(String tokenDecimal) {
		this.tokenDecimal = tokenDecimal;
	}

	@Override
	public String toString() {
		return "TokenDetailsDto [contractTokenAddress=" + tokenContractAddress + ", contractABI=" + contractABI
				+ ", tokenName=" + tokenName + ", tokenSymbol=" + tokenSymbol + ", tokenDecimal=" + tokenDecimal + "]";
	}
}
