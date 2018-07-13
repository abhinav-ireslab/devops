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

	private String clientType;

	private String tokenType;

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
	 * @return the type
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * @param tokenType
	 *            the type to set
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
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
		return "BaseApiRequest [clientCorrelationId=" + clientCorrelationId + ", tokenType=" + tokenType + "]";
	}

}
