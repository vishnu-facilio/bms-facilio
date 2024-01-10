package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class AddDashboardTabCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		
		DashboardTabContext dashboardTab = (DashboardTabContext) context.get(FacilioConstants.ContextNames.DASHBOARD_TAB);
		getDashboardTabLinkName(dashboardTab);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardTabModule().getTableName())
				.fields(FieldFactory.getDashboardTabFields());

		Map<String, Object> prop = FieldUtil.getAsProperties(dashboardTab);
		insertBuilder.addRecord(prop);
		insertBuilder.save();

		dashboardTab.setId((Long) prop.get("id"));
		context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTab);
		AddOrUpdateDashboardFilterCommand tabFilter = new AddOrUpdateDashboardFilterCommand();
		DashboardFilterContext filter = new DashboardFilterContext();
		filter.setIsTimelineFilterEnabled(true);
		filter.setDateOperator(DateOperators.CURRENT_MONTH.getOperatorId());
		filter.setDateLabel("Current Month");
		filter.setHideFilterInsideWidgets(false);
		filter.setDashboardTabId((Long) prop.get("id"));
		context.put(FacilioConstants.ContextNames.DASHBOARD_FILTER,filter);
		tabFilter.executeCommand(context);
		return false;
	}
	private void getDashboardTabLinkName(DashboardTabContext tab) throws Exception {
		if(tab!=null && tab.getLinkName()==null){
			Map<String, FacilioField> dashboardTabFields = FieldFactory.getAsMap(FieldFactory.getDashboardTabFields());
			FacilioField tabLinkName = dashboardTabFields.get(FacilioConstants.ContextNames.LINK_NAME);
			FacilioModule module = ModuleFactory.getDashboardTabModule();
			List<String> linkNames = DashboardUtil.getExistingLinkNames(module.getTableName(),tabLinkName);
			if(tab.getLinkName() == null){
				String name = tab.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
				String linkName = DashboardUtil.getLinkName(name,linkNames);
				tab.setLinkName(linkName);
			}
		}
	}

}
