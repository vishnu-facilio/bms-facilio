package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateConnectionCommand implements Command  {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ConnectionContext connectionContext = (ConnectionContext) context.get(FacilioConstants.ContextNames.CONNECTION);
		
		fillState(connectionContext);
		fillDefaultfields(connectionContext);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getConnectionModule().getTableName())
				.fields(FieldFactory.getConnectionFields())
				.andCondition(CriteriaAPI.getIdCondition(connectionContext.getId(), ModuleFactory.getConnectionModule()));
		
		updateRecordBuilder.update(FieldUtil.getAsProperties(connectionContext));
		
		return false;
	}

	private void fillState(ConnectionContext connectionContext) {
		if(connectionContext.getAuthToken() != null) {
			connectionContext.setState(ConnectionContext.State.AUTH_TOKEN_GENERATED.getValue());
		}
		else if(connectionContext.getAuthCode() != null) {
			connectionContext.setState(ConnectionContext.State.AUTHORIZED.getValue());
		}
		if(connectionContext.getClientId() != null && connectionContext.getClientSecretId() != null) {
			connectionContext.setState(ConnectionContext.State.CLIENT_ID_MAPPED.getValue());
		}
	}

	private void fillDefaultfields(ConnectionContext connectionContext) {
		
//		connectionContext.setSysModifiedBy(AccountUtil.getCurrentUser());
		connectionContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());
	}

}
