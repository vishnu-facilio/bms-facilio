package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
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
			
			GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getConnectionParamsModule().getTableName())
					.fields(FieldFactory.getConnectionParamFields());
			
			for(ConnectionParamContext connectionParam : connectionContext.getConnectionParams()) {
				connectionParam.setOrgId(AccountUtil.getCurrentOrg().getId());
				connectionParam.setConnectionId(connectionContext.getId());
				
				insert1.addRecord(FieldUtil.getAsProperties(connectionContext));
			}
			
			insert1.save();
		}
		return false;
	}


}
