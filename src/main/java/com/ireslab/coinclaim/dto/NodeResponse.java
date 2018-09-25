package com.ireslab.coinclaim.dto;

/**
 * @author iRESlab
 *
 */
public class NodeResponse {

	protected Long errorCode;
	protected String description;
	protected String resultCode;

	/**
	 * @return the errorCode
	 */
	public Long getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(Long errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode
	 *            the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeResponse [errorCode=" + errorCode + ", description=" + description + ", resultCode=" + resultCode
				+ "]";
	}
}
