package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.context.ConnectionParamContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;

public class AddConnectionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		connectionContext.setState(ConnectionContext.State.CREATED.getValue());
		
		fillDefaultfields(connectionContext);
		
		Map<String, Object> prop = FieldUtil.getAsProperties(connectionContext);
		
		GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.addRecord(prop);
		
		insert.save();
		
		connectionContext.setId((Long) prop.get("id"));
		
		if(connectionContext.getConnectionParams() != null && !connectionContext.getConnectionParams().isEmpty()) {
			
			GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getConnectionParamsModule().getTableName())
					.fields(FieldFactory.getConnectionParamFields());
			
			for(ConnectionParamContext connectionParam : connectionContext.getConnectionParams()) {
				connectionParam.setOrgId(AccountUtil.getCurrentOrg().getId());
				connectionParam.setConnectionId(connectionContext.getId());
				
				insert1.addRecord(FieldUtil.getAsProperties(connectionContext));
			}
			
			insert.save();
		}
		return false;
	}

	private void fillDefaultfields(ConnectionContext connectionContext) {

//		connectionContext.setSysCreatedBy(AccountUtil.getCurrentUser());
		connectionContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());
	}

}
