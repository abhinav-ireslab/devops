package com.ireslab.coinclaim.utils;

/**
 * @author iRESlab
 *
 */
public enum ResponseCode {

	Success(100),

	FAIL(900);

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
