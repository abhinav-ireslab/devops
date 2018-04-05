package com.ireslab.coinclaim.dto;

import java.math.BigInteger;

/**
 * @author iRESlab
 *
 */
public class TransactionDto extends NodeResponse {

	private String fromAddress;
	private Integer index;
	private String toAddress;
	private BigInteger amount;
	private String transactionReciept;

	/**
	 * @return the fromAddress
	 */
	public String getFromAddress() {
		return fromAddress;
	}

	/**
	 * @param fromAddress
	 *            the fromAddress to set
	 */
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(Integer index) {
		this.index = index;
	}

	/**
	 * @return the toAddress
	 */
	public String getToAddress() {
		return toAddress;
	}

	/**
	 * @param toAddress
	 *            the toAddress to set
	 */
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	/**
	 * @return the amount
	 */
	public BigInteger getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	/**
	 * @return the transactionReciept
	 */
	public String getTransactionReciept() {
		return transactionReciept;
	}

	/**
	 * @param transactionReciept
	 *            the transactionReciept to set
	 */
	public void setTransactionReciept(String transactionReciept) {
		this.transactionReciept = transactionReciept;
	}

}
