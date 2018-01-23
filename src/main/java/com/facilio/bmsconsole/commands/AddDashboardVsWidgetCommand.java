package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddDashboardVsWidgetCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardWidgetContext widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
		Long dashboardId = (Long) context.get(FacilioConstants.ContextNames.DASHBOARD_ID);
		if(widget != null && dashboardId != null) {			
			List<FacilioField> fields = FieldFactory.getDashbaordVsWidgetFields();
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
															.fields(fields);
			
			Map<String, Object> props = FieldUtil.getAsProperties(widget);
			props.put("dashboardId", dashboardId);
			props.put("widgetId", widget.getId());
			insertBuilder.addRecord(props);
			insertBuilder.save();
			
			context.put(FacilioConstants.ContextNames.WIDGET, widget);
		}
		
		return false;
	}

}
