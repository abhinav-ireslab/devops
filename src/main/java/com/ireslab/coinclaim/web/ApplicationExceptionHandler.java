package com.ireslab.coinclaim.web;

import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ireslab.coinclaim.exception.ApiException;
import com.ireslab.coinclaim.model.BaseApiResponse;
import com.ireslab.coinclaim.model.Error;
import com.ireslab.coinclaim.utils.ResponseCode;

/**
 * Global application exception handler
 * 
 * @author iRESlab
 *
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

	/**
	 * Api exception handler
	 * 
	 * @return
	 */
	@ExceptionHandler(value = ApiException.class)
	public ResponseEntity<BaseApiResponse> handleApiException(ApiException exp) {
		
		LOG.error("API Exception | Error description-" + ExceptionUtils.getStackTrace(exp));

		BaseApiResponse baseApiResponse = new BaseApiResponse();

		baseApiResponse.setErrors(exp.getErrors() == null
				? Arrays.asList(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error"))
				: exp.getErrors());
		
		baseApiResponse.setCode(exp.getCode() == null ? ResponseCode.GENERAL_ERROR.getCode() : exp.getCode());
		baseApiResponse.setMessage(exp.getMessage() == null ? "Internal Server Error" : exp.getMessage());
		baseApiResponse.setStatus(
				exp.getHttpStatus() == null ? HttpStatus.INTERNAL_SERVER_ERROR.value() : exp.getHttpStatus().value());

		HttpStatus httpStatus = exp.getHttpStatus();
		if (httpStatus == null) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<>(baseApiResponse, httpStatus);
	}

	/**
	 * System exception handler - Handles all other exceptions
	 * 
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<BaseApiResponse> handleSystemException(Exception exp) {

		LOG.error("Exception | Error description-" + ExceptionUtils.getStackTrace(exp));

		BaseApiResponse baseApiResponse = new BaseApiResponse();
		return new ResponseEntity<BaseApiResponse>(baseApiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
