package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateDashboardCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null) {			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getDashboardModule().getTableName())
					.fields(fields)
					.andCustomWhere(ModuleFactory.getDashboardModule().getTableName()+".ID = ?", dashboard.getId());

			Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
			int updatedRows = updateBuilder.update(props);
			
			List<DashboardWidgetContext> existingWidgets = DashboardUtil.getDashboardWidgetsFormDashboardId(dashboard.getId());
			
			JSONObject widgetMapping = new JSONObject();
			List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
			if (widgets != null && widgets.size() > 0)  {
				for (int i = 0; i < widgets.size(); i++) {
					widgetMapping.put(widgets.get(i).getId(), true);
				}
			}

			List<Long> removedWidgets = new ArrayList<Long>();
			for (int i = 0; i < existingWidgets.size(); i++) {
				if (!widgetMapping.containsKey(existingWidgets.get(i).getId())) {
					removedWidgets.add(existingWidgets.get(i).getId());
				}
			}
			
			List<DashboardWidgetContext> updatedWidgets = dashboard.getDashboardWidgets();
			if (updatedWidgets != null && updatedWidgets.size() > 0)  {
				for (int i = 0; i < updatedWidgets.size(); i++) {
					
					GenericUpdateRecordBuilder updateBuilder1 = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getDashboardVsWidgetModule().getTableName())
							.fields(FieldFactory.getDashbaordVsWidgetFields())
							.andCustomWhere(ModuleFactory.getDashboardVsWidgetModule().getTableName()+".ID = ?", updatedWidgets.get(i).getId());

					Map<String, Object> props1 = FieldUtil.getAsProperties(updatedWidgets.get(i));
					updateBuilder1.update(props1);
				}
			}
		}
		
		return false;
	}

}