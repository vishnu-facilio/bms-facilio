package com.facilio.iam.accounts.exceptions;

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
		EMAIL_ALREADY_EXISTS,
		ROLE_ID_IS_NULL,
		NOT_PERMITTED,
		USER_DOESNT_EXIST_IN_ORG,
		USER_ALREADY_EXISTS_IN_ORG,
		USER_ALREADY_DELETED,
		USER_DEACTIVATED_FROM_THE_ORG,
		ERROR_VALIDATING_CREDENTIALS,
		USER_ALREADY_EXISTS_IN_ORG_PORTAL,
		INVALID_APP_DOMAIN,
		USER_DOESNT_EXIST_IN_THIS_APP_OF_THE_ORG,
		USER_ALREADY_EXISTS_IN_APP
		;
	}
}
