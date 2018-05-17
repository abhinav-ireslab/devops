package com.ireslab.coinclaim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetails {

	private String type;

	private String address;

	private String balance;

	private String tokenCode;

	/**
	 * @param type
	 * @param address
	 * @param balance
	 */
	public AccountDetails(String type, String address, String balance) {
		super();
		this.type = type;
		this.address = address;
		this.balance = balance;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the balance
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(String balance) {
		this.balance = balance;
	}

	/**
	 * @return the tokenCode
	 */
	public String getTokenCode() {
		return tokenCode;
	}

	/**
	 * @param tokenCode
	 *            the tokenCode to set
	 */
	public void setTokenCode(String tokenCode) {
		this.tokenCode = tokenCode;
	}
}
