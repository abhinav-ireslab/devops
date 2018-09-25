package com.ireslab.coinclaim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Nitin
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

	/* Application Status Codes */
	private Integer code;

	private String message;
	private String description;
	private String moreInfo;

	public Error() {
		super();
	}

	/**
	 * @param code
	 * @param message
	 */
	public Error(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	/**
	 * @param code
	 * @param message
	 * @param description
	 */
	public Error(Integer code, String message, String description) {
		super();
		this.code = code;
		this.message = message;
		this.description = description;
	}

	/**
	 * @param code
	 * @param message
	 * @param description
	 * @param moreInfo
	 */
	public Error(Integer code, String message, String description, String moreInfo) {
		super();
		this.code = code;
		this.message = message;
		this.description = description;
		this.moreInfo = moreInfo;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public Error setCode(Integer code) {
		this.code = code;
		return this;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public Error setMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * @return the developerMessage
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the developerMessage to set
	 */
	public Error setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * @return the moreInfo
	 */
	public String getMoreInfo() {
		return moreInfo;
	}

	/**
	 * @param moreInfo
	 *            the moreInfo to set
	 */
	public Error setMoreInfo(String moreInfo) {
		this.moreInfo = moreInfo;
		return this;
	}
}
