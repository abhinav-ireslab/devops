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

	private String clientType;
	private String transactionReciept;
	
	private String gasPrice;

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
	 * @return the clientType
	 */
	public String getClientType() {
		return clientType;
	}

	/**
	 * @param clientType
	 *            the clientType to set
	 */
	public void setClientType(String clientType) {
		this.clientType = clientType;
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

	/* (non-Javadoc)
	 * @see com.ireslab.coinclaim.dto.NodeResponse#toString()
	 */
	@Override
	public String toString() {
		return "TransactionDto [fromAddress=" + fromAddress + ", index=" + index + ", toAddress=" + toAddress
				+ ", amount=" + amount + ", clientType=" + clientType + ", transactionReciept=" + transactionReciept
				+ ", gasPrice=" + gasPrice + "]";
	}
}
