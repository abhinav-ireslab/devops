package com.ireslab.coinclaim.utils;

/**
 * @author iRESlab
 *
 */
public enum ResponseCode {

	SUCCESS(100),

	GENERAL_ERROR(801),
	
	INVALID_REQUEST(802),
	
	MISSING_OR_INVALID_CLIENT_CORRELATION_ID(803),

	CLIENT_DOES_NOT_EXISTS(804),

	CLIENT_ALREADY_EXISTS(805),

	INVALID_TOKEN_AMOUNT(806),

	MISSING_OR_INVALID_BENEFICIARY_ADDRESS(807);

	private Integer code;

	/**
	 * @param code
	 */
	private ResponseCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return
	 */
	public Integer getCode() {
		return code;
	}
}
