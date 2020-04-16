package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddOrUpdateConnectedAppWidgetCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ConnectedAppWidgetContext ConnectedAppWidgetContext = (ConnectedAppWidgetContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (ConnectedAppWidgetContext != null) {
			
			if (ConnectedAppWidgetContext.getId() > 0) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
						.fields(FieldFactory.getConnectedAppWidgetsFields())
						.andCondition(CriteriaAPI.getIdCondition(ConnectedAppWidgetContext.getId(), ModuleFactory.getConnectedAppWidgetsModule()));
				
				Map<String, Object> props = FieldUtil.getAsProperties(ConnectedAppWidgetContext);
				updateBuilder.update(props);
			}
			else {
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
						.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
						.fields(FieldFactory.getConnectedAppWidgetsFields());

				Map<String, Object> props = FieldUtil.getAsProperties(ConnectedAppWidgetContext);

				insertBuilder.addRecord(props);
				insertBuilder.save();
				ConnectedAppWidgetContext.setId((Long) props.get("id"));
				
			}
			context.put(FacilioConstants.ContextNames.RECORD,ConnectedAppWidgetContext);
		}
		
		return false;
	}

}
