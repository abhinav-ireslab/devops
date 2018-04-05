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
public class TransferTokensRequest extends BaseApiRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 483641603937676982L;

	private String beneficiaryAddress;

	private String noOfTokens;

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

}
