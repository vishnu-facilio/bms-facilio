package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;

public class DeleteConnectedAppCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if (connectedAppId > 0) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));

			builder.delete();
			
			builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));

			builder.delete();
			
			builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getVariablesModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));

			builder.delete();
			
			builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));

			builder.delete();
			
			builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getConnectedAppsModule().getTableName())
					.andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
			
			builder.delete();
			
		}
		
		return false;
	}

}
