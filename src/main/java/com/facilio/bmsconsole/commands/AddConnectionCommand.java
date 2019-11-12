package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectionApiContext;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionParamContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddConnectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		connectionContext.setState(ConnectionContext.State.CREATED.getValue());
		
		ConnectionUtil.addConnection(connectionContext);
		
		if(connectionContext.getConnectionParams() != null && !connectionContext.getConnectionParams().isEmpty()) {
			
			for(ConnectionParamContext connectionParam : connectionContext.getConnectionParams()) {
				connectionParam.setConnectionId(connectionContext.getId());
				ConnectionUtil.addConnectionParams(connectionParam);
			}
			
		}
		
		if(connectionContext.getConnectionApis() != null && !connectionContext.getConnectionApis().isEmpty()) {
			
			for(ConnectionApiContext connectionApi : connectionContext.getConnectionApis()) {
				
				connectionApi.setConnectionId(connectionContext.getId());
				ConnectionUtil.addConnectionApi(connectionApi);
				
				if(connectionApi.getConnectionParams() != null && !connectionApi.getConnectionParams().isEmpty()) {
					
					for(ConnectionParamContext connectionParam : connectionApi.getConnectionParams()) {
						connectionParam.setConnectionId(connectionContext.getId());
						connectionParam.setConnectionApiId(connectionApi.getId());
						ConnectionUtil.addConnectionParams(connectionParam);
					}
				}
			}
		}
		return false;
	}


}
