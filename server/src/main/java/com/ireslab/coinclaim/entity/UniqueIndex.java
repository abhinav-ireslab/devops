package com.ireslab.coinclaim.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author iRESlab
 *
 */
@Entity
@Table(name = "unique_index")
public class UniqueIndex {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "unique_company_index")
	private BigInteger uniqueCompanyIndex;

	@Column(name = "unique_user_index")
	private BigInteger uniqueUserIndex;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the uniqueCompanyIndex
	 */
	public BigInteger getUniqueCompanyIndex() {
		return uniqueCompanyIndex;
	}

	/**
	 * @param uniqueCompanyIndex
	 *            the uniqueCompanyIndex to set
	 */
	public void setUniqueCompanyIndex(BigInteger uniqueCompanyIndex) {
		this.uniqueCompanyIndex = uniqueCompanyIndex;
	}

	/**
	 * @return the uniqueUserIndex
	 */
	public BigInteger getUniqueUserIndex() {
		return uniqueUserIndex;
	}

	/**
	 * @param uniqueUserIndex
	 *            the uniqueUserIndex to set
	 */
	public void setUniqueUserIndex(BigInteger uniqueUserIndex) {
		this.uniqueUserIndex = uniqueUserIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UniqueIndex [id=" + id + ", uniqueCompanyIndex=" + uniqueCompanyIndex + ", uniqueUserIndex="
				+ uniqueUserIndex + "]";
	}
}
