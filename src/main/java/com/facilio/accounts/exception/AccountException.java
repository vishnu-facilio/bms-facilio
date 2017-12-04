package com.facilio.accounts.exception;

public class AccountException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ErrorCode errorCode;

	public AccountException(ErrorCode errorCode, String message){
		super(message);
		this.errorCode=errorCode;
	}

	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
	
	public String getMessage() {
		return "[" + getErrorCode().toString() + "] " + super.getMessage();
	}
	
	public String toString() {
		return getMessage();
	}
	
	public static enum ErrorCode {
		ORG_DOMAIN_ALREADY_EXISTS,
		EMAIL_ALREADY_EXISTS;
	}
}
