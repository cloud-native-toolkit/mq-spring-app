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

@RestController
@EnableJms
public class MQClientController {

	@Autowired
	private MQService mqService;

	public MQClientController(MQService mqService) {
		this.mqService = mqService;
	}

	@GetMapping("/api/send-hello-world")
	ResponseData send() {
		String dataSentToQueue = mqService.sendHelloWorld();
		ResponseData responseData = new ResponseData("OK", "Successfully sent record to MQ", dataSentToQueue);
		return responseData;
	}

	@GetMapping("/api/recv")
	ResponseData recv() {
		String dataReceivedFromQueue = mqService.receiveMessage();
		ResponseData responseData = new ResponseData("OK", "Successfully received record from MQ",
				dataReceivedFromQueue);
		return responseData;

	}

	@PostMapping("/api/send-json")
	ResponseData sendPost(@RequestBody Map<String, Object> requestMap) {
		String dataSentToQueue = mqService.sendJson(requestMap);
		ResponseData responseData = new ResponseData("OK", "Successfully sent record to MQ", dataSentToQueue);
		return responseData;
	}
}
