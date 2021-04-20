package com.ibm.mqclient.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ibm.mqclient.exceptions.AppException;
import com.ibm.mqclient.model.ErrorResponse;
import com.ibm.mqclient.service.MQService;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	final Logger LOG = LoggerFactory.getLogger(MQService.class);
	
	@ExceptionHandler({ AppException.class })
	protected ResponseEntity<Object> handleBadRequestInputException(AppException e, WebRequest request) {
		LOG.error("Application Exception.", e);
		ErrorResponse error = new ErrorResponse(e.getErrorCode(), e.getLocalizedMessage());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return handleExceptionInternal(e, error, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}
