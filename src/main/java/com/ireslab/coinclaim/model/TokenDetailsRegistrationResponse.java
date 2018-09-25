package com.ireslab.coinclaim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author iRESlab
 *
 */
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetailsRegistrationResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param status
	 * @param code
	 * @param message
	 */
	public TokenDetailsRegistrationResponse(Integer status, Integer code, String message) {
		super(status, code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param errors
	 */
	public TokenDetailsRegistrationResponse(Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public TokenDetailsRegistrationResponse(Integer status, Integer code, String message, List<Error> errors) {
		super(status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param refId
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public TokenDetailsRegistrationResponse(String refId, Integer status, Integer code, String message, List<Error> errors) {
		super(refId, status, code, message, errors);
		// TODO Auto-generated constructor stub
	}
}
