package com.ireslab.coinclaim.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * Custom application exception
 * 
 * @author iRESlab
 *
 */
public class ApiException extends RuntimeException {

	private static final long serialVersionUID = -2631991820460383338L;

	private String refId;
	private HttpStatus httpStatus;

	private Integer code;
	private String message;

	private List<Error> errors;

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId
	 *            the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	/**
	 * @return the httpStatus
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	/**
	 * @param httpStatus
	 *            the httpStatus to set
	 */
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
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
	public void setCode(Integer code) {
		this.code = code;
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
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the errors
	 */
	public List<Error> getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
