package com.ibm.mqclient.model;

public class ErrorResponse {
    public ErrorResponse(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	private String code;
    
    private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
