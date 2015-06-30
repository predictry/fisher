package com.predictry.fisher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.fisher.domain.ErrorMessage;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * General error handler controller.
	 */
	@ExceptionHandler(value={Exception.class, RuntimeException.class})
	public ErrorMessage error(Exception ex) {
		log.error("Handling global exception", ex);
		ErrorMessage error = new ErrorMessage(ex.getMessage());
		return error;
	}
	
}
