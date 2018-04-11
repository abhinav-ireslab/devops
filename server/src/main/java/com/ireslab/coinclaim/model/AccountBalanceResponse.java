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
public class AccountBalanceResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860997560925100629L;

	private String accountBalance;

	/**
	 * @param status
	 * @param code
	 * @param message
	 */
	public AccountBalanceResponse(Integer status, Integer code, String message) {
		super(status, code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param errors
	 */
	public AccountBalanceResponse(Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public AccountBalanceResponse(Integer status, Integer code, String message, List<Error> errors) {
		super(status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param refId
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public AccountBalanceResponse(String refId, Integer status, Integer code, String message, List<Error> errors) {
		super(refId, status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param address
	 * @param status
	 * @param code
	 * @param errors
	 */
	public AccountBalanceResponse(String accountBalance, Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		this.accountBalance = accountBalance;
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
