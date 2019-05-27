package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ConnectionAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LogManager.getLogger(ConnectionAction.class.getName());
	
	ConnectionContext connectionContext;
	
	public ConnectionContext getConnectionContext() {
		return connectionContext;
	}

	public void setConnectionContext(ConnectionContext connectionContext) {
		this.connectionContext = connectionContext;
	}
	
	public String addConnection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
		
		Chain chain = TransactionChainFactory.getAddConnectionChain();
		chain.execute(context);
		
		setResult("connection", connectionContext);
		return SUCCESS;
	}
	
	public String updateConnection() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
		
		Chain chain = TransactionChainFactory.getUpdateConnectionChain();
		chain.execute(context);
		
		setResult("connection", connectionContext);
		return SUCCESS;
	}

	public String deleteConnection() throws Exception {
	
	FacilioContext context = new FacilioContext();
	context.put(FacilioConstants.ContextNames.CONNECTION, connectionContext);
	
	Chain chain = TransactionChainFactory.getDeleteConnectionChain();
	chain.execute(context);
	
	setResult("connection", connectionContext);
	return SUCCESS;
	}

}
