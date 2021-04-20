package com.ibm.mqclient.exceptions;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = -200052554991919634L;
	
	private String errorCode;
	
	public AppException (final String errorCode, final String message, Throwable cause) {
		super(message,cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
}
