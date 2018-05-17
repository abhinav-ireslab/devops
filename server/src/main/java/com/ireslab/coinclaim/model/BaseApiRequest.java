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

	private String clientCorrelationId;

	private String type;

	private String tokenCode;

	/**
	 * @return the companyCorrelationId
	 */
	public String getClientCorrelationId() {
		return clientCorrelationId;
	}

	/**
	 * @param clientCorrelationId
	 *            the companyCorrelationId to set
	 */
	public void setClientCorrelationId(String clientCorrelationId) {
		this.clientCorrelationId = clientCorrelationId;
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

	@Override
	public String toString() {
		return "BaseApiRequest [clientCorrelationId=" + clientCorrelationId + ", type=" + type + "]";
	}

}
