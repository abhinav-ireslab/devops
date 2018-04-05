package com.ireslab.coinclaim.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author iRESlab
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseApiRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4181869035260633029L;

	private String companyCorrelationId;

	private String type;

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

	@Override
	public String toString() {
		return "BaseApiRequest [companyCorrelationId=" + companyCorrelationId + ", type=" + type + "]";
	}

}
