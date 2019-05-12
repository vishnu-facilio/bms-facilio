package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

public class AddOrUpdateConnectedAppCommand  implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		ConnectedAppContext connectedAppContext = (ConnectedAppContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (connectedAppContext != null) {
			
			if (connectedAppContext.getId() > 0) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getConnectedAppsModule().getTableName())
						.fields(FieldFactory.getConnectedAppFields())
						.andCondition(CriteriaAPI.getIdCondition(connectedAppContext.getId(), ModuleFactory.getConnectedAppsModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(connectedAppContext);
				int updatedRows = updateBuilder.update(props);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getConnectedAppsModule().getTableName())
						.fields(FieldFactory.getConnectedAppFields());

				Map<String, Object> props = FieldUtil.getAsProperties(connectedAppContext);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				long recorId = (Long) props.get("id");
				connectedAppContext.setId(recorId);
			}
		}
		
		return false;
	}
}
