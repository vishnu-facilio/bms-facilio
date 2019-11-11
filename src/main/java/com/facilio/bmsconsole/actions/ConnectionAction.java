package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class ConnectionAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(ConnectionAction.class.getName());
	
	ConnectionContext connectionContext;
	
	String state;
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ConnectionContext getConnectionContext() {
		return connectionContext;
	}

	public void setConnectionContext(ConnectionContext connectionContext) {
		this.connectionContext = connectionContext;
	}
	
	public String addConnection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
		
		FacilioChain chain = TransactionChainFactory.getAddConnectionChain();
		chain.execute(context);
		
		setResult("connection", connectionContext);
		return SUCCESS;
	}
	
	public String addAuthorizationCode() throws Exception {
		
		if(code == null) {
			throw new IllegalArgumentException("Auth Code is Empty");
		}
		connectionContext = ConnectionUtil.getConnectionFromSecretStateString(state);
		connectionContext.setAuthCode(code);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
		
		FacilioChain chain = TransactionChainFactory.getUpdateConnectionChain();
		chain.execute(context);
		
		setResult("connection", connectionContext);
		return SUCCESS;
	}
	
	public String updateConnection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
		
		FacilioChain chain = TransactionChainFactory.getUpdateConnectionChain();
		chain.execute(context);
		
		setResult("connection", connectionContext);
		return SUCCESS;
	}
	
	public String getAllConnection() throws Exception {
		
		setResult("connections", ConnectionUtil.getAllConnections());
		
		return SUCCESS;
	}

	public String deleteConnection() throws Exception {
	
	FacilioContext context = new FacilioContext();
	context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
	
	FacilioChain chain = TransactionChainFactory.getDeleteConnectionChain();
	chain.execute(context);
	
	setResult("connection", connectionContext);
	return SUCCESS;
	}

}
