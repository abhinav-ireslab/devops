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
public class AccountBalanceResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860997560925100629L;

	private String accountBalance;

	public AccountBalanceResponse(Integer status, Integer code, String message) {
		super(status, code, message);
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
