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
public class TransferTokensResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3014657783343431256L;

	private String accountBalance;

	/**
	 * @param status
	 * @param code
	 * @param message
	 */
	public TransferTokensResponse(Integer status, Integer code, String message) {
		super(status, code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param errors
	 */
	public TransferTokensResponse(Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public TransferTokensResponse(Integer status, Integer code, String message, List<Error> errors) {
		super(status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the accountBalance
	 */
	public String getAccountBalance() {
		return accountBalance;
	}

	/**
	 * @param accountBalance
	 *            the accountBalance to set
	 */
	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}
}
