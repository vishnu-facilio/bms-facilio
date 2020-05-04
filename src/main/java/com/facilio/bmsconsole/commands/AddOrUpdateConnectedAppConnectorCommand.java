package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppConnectorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateConnectedAppConnectorCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectedAppConnectorContext ConnectedAppConnector = (ConnectedAppConnectorContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (ConnectedAppConnector != null) {
			
			if (ConnectedAppConnector.getId() > 0) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
						.fields(FieldFactory.getConnectedAppConnectorsFields())
						.andCondition(CriteriaAPI.getIdCondition(ConnectedAppConnector.getId(), ModuleFactory.getConnectedAppConnectorsModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(ConnectedAppConnector);
				updateBuilder.update(props);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getConnectedAppConnectorsModule().getTableName())
						.fields(FieldFactory.getConnectedAppConnectorsFields());

				Map<String, Object> props = FieldUtil.getAsProperties(ConnectedAppConnector);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				ConnectedAppConnector.setId((Long) props.get("id"));
				
			}
			context.put(FacilioConstants.ContextNames.RECORD,ConnectedAppConnector);
		}
		
		return false;
	}

}
