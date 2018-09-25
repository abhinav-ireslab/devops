package com.ireslab.coinclaim.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author iRESlab
 *
 */
@Entity
@Table(name = "company_token")
public class CompanyToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id")
	private Integer tokenId;

	@Column(name = "token_name")
	private String tokenName;

	@Column(name = "token_symbol")
	private String tokenSymbol;

	@Column(name = "token_decimals")
	private BigInteger tokenDecimals;

	@Column(name = "token_contract_address")
	private String tokenContractAddress;

	@Column(name = "token_contract_binary")
	private String tokenContractBinary;

	@ManyToOne
	@JoinColumn(name = "company_id")
	private CompanyAccount companyAccount;

	/**
	 * @return the tokenId
	 */
	public Integer getTokenId() {
		return tokenId;
	}

	/**
	 * @param tokenId
	 *            the tokenId to set
	 */
	public void setTokenId(Integer tokenId) {
		this.tokenId = tokenId;
	}

	/**
	 * @return the tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName
	 *            the tokenName to set
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return the tokenSymbol
	 */
	public String getTokenSymbol() {
		return tokenSymbol;
	}

	/**
	 * @param tokenSymbol
	 *            the tokenSymbol to set
	 */
	public void setTokenSymbol(String tokenSymbol) {
		this.tokenSymbol = tokenSymbol;
	}

	/**
	 * @return the tokenDecimals
	 */
	public BigInteger getTokenDecimals() {
		return tokenDecimals;
	}

	/**
	 * @param tokenDecimals
	 *            the tokenDecimals to set
	 */
	public void setTokenDecimals(BigInteger tokenDecimals) {
		this.tokenDecimals = tokenDecimals;
	}

	/**
	 * @return the tokenContractAddress
	 */
	public String getTokenContractAddress() {
		return tokenContractAddress;
	}

	/**
	 * @param tokenContractAddress
	 *            the tokenContractAddress to set
	 */
	public void setTokenContractAddress(String tokenContractAddress) {
		this.tokenContractAddress = tokenContractAddress;
	}

	/**
	 * @return the tokenContractBinary
	 */
	public String getTokenContractBinary() {
		return tokenContractBinary;
	}

	/**
	 * @param tokenContractBinary
	 *            the tokenContractBinary to set
	 */
	public void setTokenContractBinary(String tokenContractBinary) {
		this.tokenContractBinary = tokenContractBinary;
	}

	/**
	 * @return the companyId
	 */
	public CompanyAccount getCompanyAccount() {
		return companyAccount;
	}

	/**
	 * @param companyAccount
	 *            the companyId to set
	 */
	public void setCompanyAccount(CompanyAccount companyAccount) {
		this.companyAccount = companyAccount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CompanyToken [tokenId=" + tokenId + ", tokenName=" + tokenName + ", tokenSymbol=" + tokenSymbol
				+ ", tokenDecimals=" + tokenDecimals + ", tokenContractAddress=" + tokenContractAddress
				+ ", tokenContractBinary=" + tokenContractBinary + "]";
	}
}
