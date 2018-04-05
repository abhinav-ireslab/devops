package com.ireslab.coinclaim.dto;

/**
 * @author iRESlab
 *
 */
public class NodeResponse {

	private Long errorCode;
	private String description;
	private String resultCode;

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
}
