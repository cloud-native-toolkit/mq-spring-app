package com.ibm.mqclient.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "This is the response data from the mq api operations that exeucte successfully.")
public class ResponseData {

	public ResponseData(String status, String statusMessage, String data) {
		super();
		this.status = status;
		this.statusMessage = statusMessage;
		this.data = data;
	}

    @ApiModelProperty (value = "Value is OK if operation executed successfully.")
	private String status;

    @ApiModelProperty (value = "Description of the operation that completed.")
	private String statusMessage;
	
    @ApiModelProperty (value = "Data sent to the queue or received from the queue.")
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
