package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class GetConnectedAppWidgetListCommand extends FacilioCommand{
	
	private static Logger LOGGER = LogManager.getLogger(GetConnectedAppWidgetListCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getConnectedAppWidgetsFields())
				.table(ModuleFactory.getConnectedAppWidgetsModule().getTableName())
				.innerJoin("ConnectedApps")
				.on("ConnectedApp_Widgets.CONNECTEDAPP_ID=ConnectedApps.ID")
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "true", BooleanOperators.IS));
		;
		
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			selectBuilder.andCriteria(filterCriteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<ConnectedAppWidgetContext> connectedAppWidgets = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				connectedAppWidgets.add(FieldUtil.getAsBeanFromMap(prop, ConnectedAppWidgetContext.class));
			}
		}
		
		context.put(FacilioConstants.ContextNames.RECORD_LIST, connectedAppWidgets);
		return false;
	}
}
