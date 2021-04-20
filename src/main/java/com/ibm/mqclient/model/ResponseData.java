package com.ibm.mqclient.model;

public class ResponseData {

	public ResponseData(String status, String statusMessage, String data) {
		super();
		this.status = status;
		this.statusMessage = statusMessage;
		this.data = data;
	}

	private String status;

	private String statusMessage;

	private String data;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String message) {
		this.statusMessage = message;
	}

	public String getData() {
		return data;
	}

	public void setData(String body) {
		this.data = body;
	}

}
