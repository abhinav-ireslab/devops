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

	@Column(name = "unique_index")
	private BigInteger uniqueIndex;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigInteger getUniqueIndex() {
		return uniqueIndex;
	}

	public void setUniqueIndex(BigInteger uniqueIndex) {
		this.uniqueIndex = uniqueIndex;
	}

	@Override
	public String toString() {
		return "UniqueIndex [id=" + id + ", uniqueIndex=" + uniqueIndex + "]";
	}
}
