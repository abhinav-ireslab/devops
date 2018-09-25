package com.ireslab.coinclaim.entity;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author iRESlab
 *
 */
@Entity
@Table(name = "user_account")
public class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_account_id")
	private Integer userAccountId;

	@Column(name = "user_correlation_id")
	private String userCorrelationId;

	@Column(name = "child_index")
	private BigInteger childIndex;

	@Column(name = "btc_address")
	private String btcAddress;

	@Column(name = "eth_address")
	private String ethAddress;

	@Column(name = "date_modified", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	/**
	 * @return the accountId
	 */
	public Integer getCompanyAccountId() {
		return userAccountId;
	}

	/**
	 * @param companyAccountId
	 *            the accountId to set
	 */
	public void setCompanyAccountId(Integer companyAccountId) {
		this.userAccountId = companyAccountId;
	}

	/**
	 * @return the clientCorrelationId
	 */
	public String getUserCorrelationId() {
		return userCorrelationId;
	}

	/**
	 * @param userCorrelationId
	 *            the companyCorrelationId to set
	 */
	public void setUserCorrelationId(String userCorrelationId) {
		this.userCorrelationId = userCorrelationId;
	}

	/**
	 * @return the childIndex
	 */
	public BigInteger getChildIndex() {
		return childIndex;
	}

	/**
	 * @param childIndex
	 *            the childIndex to set
	 */
	public void setChildIndex(BigInteger childIndex) {
		this.childIndex = childIndex;
	}

	/**
	 * @return the btcAddress
	 */
	public String getBtcAddress() {
		return btcAddress;
	}

	/**
	 * @param btcAddress
	 *            the btcAddress to set
	 */
	public void setBtcAddress(String btcAddress) {
		this.btcAddress = btcAddress;
	}

	/**
	 * @return the ethAddress
	 */
	public String getEthAddress() {
		return ethAddress;
	}

	/**
	 * @param ethAddress
	 *            the ethAddress to set
	 */
	public void setEthAddress(String ethAddress) {
		this.ethAddress = ethAddress;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate
	 *            the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CompanyAccount [accountId=" + userAccountId + ", companyCorrelationId=" + userCorrelationId
				+ ", childIndex=" + childIndex + ", btcAddress=" + btcAddress + ", modifiedDate=" + modifiedDate + "]";
	}
}
