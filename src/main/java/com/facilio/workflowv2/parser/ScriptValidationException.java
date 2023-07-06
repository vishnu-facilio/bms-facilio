package com.facilio.workflowv2.parser;

public class ScriptValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	
	public ScriptValidationException() {
		
	}
	
	public ScriptValidationException(String e) {
		super(e);
	}
}
