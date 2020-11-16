package com.facilio.bmsconsole.context;

public interface GrantTypeInterface {

	public void updateAuthToken(ConnectionContext connectionContext) throws Exception;
	public void validateConnection(ConnectionContext connectionContext) throws Exception;
}
