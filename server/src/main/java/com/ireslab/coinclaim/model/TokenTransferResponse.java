package com.ireslab.coinclaim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author iRESlab
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenTransferResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3014657783343431256L;

	private List<AccountDetails> accountDetails;

	/**
	 * @param status
	 * @param code
	 * @param message
	 */
	public TokenTransferResponse(Integer status, Integer code, String message) {
		super(status, code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param errors
	 */
	public TokenTransferResponse(Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public TokenTransferResponse(Integer status, Integer code, String message, List<Error> errors) {
		super(status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the accountDetails
	 */
	public List<AccountDetails> getAccountDetails() {
		return accountDetails;
	}

	/**
	 * @param accountDetails
	 *            the accountDetails to set
	 */
	public void setAccountDetails(List<AccountDetails> accountDetails) {
		this.accountDetails = accountDetails;
	}
}
