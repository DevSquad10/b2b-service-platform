package com.devsquad10.shipping.application.exception;

public class HubIdNotFoundException extends RuntimeException {
	public HubIdNotFoundException(String message) {
		super(message);
	}
}
