package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteDashboardTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long dashboardTabId = (Long) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB_ID);
		if(dashboardTabId != null) {

			List<DashboardWidgetContext> dashboard_tab_widgets = DashboardUtil.getDashboardWidgetsFormDashboardIdOrTabId(null, dashboardTabId);
			if(dashboard_tab_widgets != null)
			{
				List<Long> deleteWidgetsList = new ArrayList<>();
				for(DashboardWidgetContext widgetContext : dashboard_tab_widgets)
				{
					if(widgetContext.getDashboardId() == null) {
						deleteWidgetsList.add(widgetContext.getId());
					}
					GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getWidgetModule().getTableName())
							.fields(FieldFactory.getWidgetFields())
							.andCondition(CriteriaAPI.getIdCondition(widgetContext.getId(), ModuleFactory.getWidgetModule()))
							.ignoreSplNullHandling();
					Map<String, Object> props1 = FieldUtil.getAsProperties(widgetContext);
					props1.put("dashboardTabId",null);
					updateBuilder.update(props1);
				}
				if(deleteWidgetsList.size()>0)
				{
					GenericDeleteRecordBuilder genericDeleteRecordBuilder = new GenericDeleteRecordBuilder();
					genericDeleteRecordBuilder.table(ModuleFactory.getWidgetModule().getTableName())
							.andCondition(CriteriaAPI.getCondition(ModuleFactory.getWidgetModule().getTableName() + ".ID", "ID", StringUtils.join(deleteWidgetsList, ","), StringOperators.IS));
					genericDeleteRecordBuilder.delete();
				}
			}
			Long dashboardId = DashboardUtil.getDashboardFromTabId(dashboardTabId);
			GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
			
			deleteRecordBuilder.table(ModuleFactory.getDashboardTabModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(dashboardTabId, ModuleFactory.getDashboardTabModule()));
			deleteRecordBuilder.delete();

			if(dashboardId != null)
			{
				DashboardContext dashboard = DashboardUtil.getDashboardContextFromDashboardId(dashboardId);
				if(dashboard !=null)
				{
					dashboard.setTabEnabled(false);
					DashboardUtil.updateDashboard(dashboard);
				}
			}
		}
		return false;
	}

}