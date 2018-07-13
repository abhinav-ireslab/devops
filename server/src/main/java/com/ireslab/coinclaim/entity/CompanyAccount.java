package com.ireslab.coinclaim.entity;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
	@Column(name = "company_account_id")
	private Integer companyAccountId;

	@Column(name = "company_correlation_id")
	private String companyCorrelationId;

	@Column(name = "child_index")
	private BigInteger childIndex;

	@Column(name = "btc_address")
	private String btcAddress;

	@Column(name = "eth_address")
	private String ethAddress;

	@Column(name = "date_modified", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@OneToMany(mappedBy = "companyAccount")
	private List<CompanyToken> companyTokens;

	/**
	 * @return the companyAccountId
	 */
	public Integer getCompanyAccountId() {
		return companyAccountId;
	}

	/**
	 * @param companyAccountId
	 *            the companyAccountId to set
	 */
	public void setCompanyAccountId(Integer companyAccountId) {
		this.companyAccountId = companyAccountId;
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

	/**
	 * @return the companyTokens
	 */
	public List<CompanyToken> getCompanyTokens() {
		return companyTokens;
	}

	/**
	 * @param companyTokens
	 *            the companyTokens to set
	 */
	public void setCompanyTokens(List<CompanyToken> companyTokens) {
		this.companyTokens = companyTokens;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CompanyAccount [companyAccountId=" + companyAccountId + ", companyCorrelationId=" + companyCorrelationId
				+ ", childIndex=" + childIndex + ", btcAddress=" + btcAddress + ", ethAddress=" + ethAddress
				+ ", modifiedDate=" + modifiedDate + ", companyTokens=" + companyTokens + "]";
	}
}
