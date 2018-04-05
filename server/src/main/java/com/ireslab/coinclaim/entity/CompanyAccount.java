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
@Table(name = "company_account")
public class CompanyAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "account_id")
	private Integer accountId;

	@Column(name = "company_correlation_id")
	private String companyCorrelationId;

	@Column(name = "child_index")
	private BigInteger childIndex;

	@Column(name = "btc_address")
	private String btcAddress;

	@Column(name = "date_modified", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	/**
	 * @return the accountId
	 */
	public Integer getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId
	 *            the accountId to set
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the companyCorrelationId
	 */
	public String getCompanyCorrelationId() {
		return companyCorrelationId;
	}

	/**
	 * @param companyCorrelationId
	 *            the companyCorrelationId to set
	 */
	public void setCompanyCorrelationId(String companyCorrelationId) {
		this.companyCorrelationId = companyCorrelationId;
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
		return "CompanyAccount [accountId=" + accountId + ", companyCorrelationId=" + companyCorrelationId
				+ ", childIndex=" + childIndex + ", btcAddress=" + btcAddress + ", modifiedDate=" + modifiedDate + "]";
	}
}
