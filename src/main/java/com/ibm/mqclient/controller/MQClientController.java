package com.ibm.mqclient.controller;


import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.mqclient.model.ResponseData;
import com.ibm.mqclient.service.MQService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;


@RestController
@EnableJms
@Api(description = "Set of endpoints for putting and getting messages to an MQ queue.")
public class MQClientController {

	@Autowired
	private MQService mqService;

	public MQClientController(MQService mqService) {
		this.mqService = mqService;
	}
	
	@GetMapping("/api/send-hello-world")
	@ApiOperation(value = "Put a hello world message on the MQ queue.", notes = "This api puts a hello world text message on the MQ queue.")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully put message on queue."), @ApiResponse(code = 500, message = "Error putting message on queue.")})	
	ResponseData send() {
		String dataSentToQueue = mqService.sendHelloWorld();
		ResponseData responseData = new ResponseData("OK", "Successfully sent record to MQ", dataSentToQueue);
		return responseData;
	}

	@GetMapping("/api/recv")
	@ApiOperation(value = "Receive a message from the MQ queue.", notes = "This api receives the message at the top of the MQ queue.")	
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully received message from the queue."), @ApiResponse(code = 500, message = "Error getting message from the queue.")})	
	ResponseData recv() {
		String dataReceivedFromQueue = mqService.receiveMessage();
		ResponseData responseData = new ResponseData("OK", "Successfully received record from MQ",
				dataReceivedFromQueue);
		return responseData;

	}

	@PostMapping("/api/send-json")
	@ApiOperation(value = "Put a json message on the MQ queue.", notes = "This api puts a json message on the MQ queue.")	
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully put message on queue."), @ApiResponse(code = 500, message = "Error putting message on queue.")})		
	ResponseData sendPost(@RequestBody Map<String, Object> requestMap) {
		String dataSentToQueue = mqService.sendJson(requestMap);
		ResponseData responseData = new ResponseData("OK", "Successfully sent record to MQ", dataSentToQueue);
		return responseData;
	}
}
