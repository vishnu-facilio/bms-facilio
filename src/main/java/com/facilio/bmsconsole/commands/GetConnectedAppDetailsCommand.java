package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.ConnectedAppSAMLContext;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetConnectedAppDetailsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long connectedAppId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if (connectedAppId > 0) {
			ConnectedAppContext connectedAppContext = null;
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppFields())
					.table(ModuleFactory.getConnectedAppsModule().getTableName())
			        .andCondition(CriteriaAPI.getIdCondition(connectedAppId, ModuleFactory.getConnectedAppsModule()));
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				connectedAppContext = FieldUtil.getAsBeanFromMap(props.get(0), ConnectedAppContext.class);
			}
			
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppSAMLFields())
					.table(ModuleFactory.getConnectedAppSAMLModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID","connectedAppId",String.valueOf(connectedAppId), NumberOperators.EQUALS));
			Map<String, Object> props1 = selectBuilder.fetchFirst();
			
			if (props1 != null && !props1.isEmpty()) {
				connectedAppContext.setConnectedAppSAML(FieldUtil.getAsBeanFromMap(props1, ConnectedAppSAMLContext.class));
			}
			selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppWidgetsFields())
					.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("CONNECTEDAPP_ID", "connectedAppId", String.valueOf(connectedAppId), NumberOperators.EQUALS))
					;
			props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				List<ConnectedAppWidgetContext> connectedAppWidgetsList = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					ConnectedAppWidgetContext connectedAppWidget = FieldUtil.getAsBeanFromMap(prop, ConnectedAppWidgetContext.class);
					if(connectedAppWidget.getCriteriaId() > 0){
						connectedAppWidget.setCriteria(CriteriaAPI.getCriteria(connectedAppWidget.getCriteriaId()));
					}
					connectedAppWidgetsList.add(connectedAppWidget);
				}
				connectedAppContext.setConnectedAppWidgetsList(connectedAppWidgetsList);
			}
			context.put(FacilioConstants.ContextNames.RECORD, connectedAppContext);
			
		}
		return false;
	}

}
