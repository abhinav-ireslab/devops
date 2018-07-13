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

	COMPANY_DOES_NOT_EXISTS(804),

	COMPANY_ALREADY_EXISTS(805),

	INVALID_TOKEN_AMOUNT(806),

	MISSING_OR_INVALID_USER_CORRELATION_ID(807),

	USER_DOES_NOT_EXISTS(808),

	USER_ALREADY_EXISTS(809),

	TOKEN_ALREADY_EXISTS(810),

	TOKEN_DOES_NOT_EXISTS(811),

	INVALID_CLIENT_TYPE(812),

	INVALID_TOKEN_TYPE(813),

	INVALID_TOKEN_DETAILS(814);

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
