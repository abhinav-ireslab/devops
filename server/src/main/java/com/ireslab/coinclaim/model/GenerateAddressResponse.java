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
public class GenerateAddressResponse extends BaseApiResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860997560925100629L;

	private String type;

	private String address;

	/**
	 * @param status
	 * @param code
	 * @param message
	 */
	public GenerateAddressResponse(Integer status, Integer code, String message) {
		super(status, code, message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param errors
	 */
	public GenerateAddressResponse(Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param code
	 * @param message
	 * @param errors
	 */
	public GenerateAddressResponse(Integer status, Integer code, String message, List<Error> errors) {
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
	public GenerateAddressResponse(String refId, Integer status, Integer code, String message, List<Error> errors) {
		super(refId, status, code, message, errors);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param type
	 * @param address
	 * @param status
	 * @param code
	 * @param errors
	 */
	public GenerateAddressResponse(String type, String address, Integer status, Integer code, List<Error> errors) {
		super(status, code, errors);
		this.type = type;
		this.address = address;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
