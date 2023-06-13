package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardContext.DashboardPublishStatus;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AddDashboardCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		DashboardContext dashboard = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		if(dashboard != null)
		{
			if (dashboard.getClientMetaJsonString() != null)
			{
				JSONParser parser = new JSONParser();
				JSONObject client_meta_json = (JSONObject) parser.parse(dashboard.getClientMetaJsonString());
				if (client_meta_json != null && !client_meta_json.containsKey("isShow")) {
					client_meta_json.put("isShow", true);
					dashboard.setClientMetaJsonString(client_meta_json.toJSONString());
				}
			}
			else
			{
				JSONObject temp = new JSONObject();
				temp.put("isShow", true);
				dashboard.setClientMetaJsonString(temp.toJSONString());
			}
			dashboard.setPublishStatus(DashboardPublishStatus.NONE.ordinal());
			dashboard.setCreatedByUserId(AccountUtil.getCurrentUser().getId());
			dashboard.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			dashboard.setCreatedBy(AccountUtil.getCurrentUser().getPeopleId());
			dashboard.setCreatedTime(System.currentTimeMillis());
			
			List<FacilioField> fields = FieldFactory.getDashboardFields();
			
			Boolean isSkip = (Boolean)context.get(FacilioConstants.ContextNames.IS_SKIP_LINKNAME_CHECK);
			
			if(isSkip == null || !isSkip) {
			   getDashboardLinkName(dashboard);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getDashboardModule().getTableName())
					.fields(fields);

			Map<String, Object> props = FieldUtil.getAsProperties(dashboard);
			insertBuilder.addRecord(props);
			insertBuilder.save();

			dashboard.setId((Long) props.get("id"));
			context.put(FacilioConstants.ContextNames.DASHBOARD, dashboard);
		}
		
		return false;
	}

	private void getDashboardLinkName(DashboardContext dashboard) throws Exception {
		
		String linkName = dashboard.getDashboardName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		
		int i=1;
		String temp = linkName;
		while(true) {
			
			if(DashboardUtil.getDashboard(temp,dashboard.getModuleId()) == null && (!DashboardUtil.RESERVED_DASHBOARD_LINK_NAME.contains(temp))) {
				dashboard.setLinkName(temp);
				break;
			}
			else {
				temp = linkName + i++;
			}
		}
	}

}