package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;

public class PreventiveMaintenanceSummaryCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Preventive_Maintenance")
				.andCustomWhere("Preventive_Maintenance.ORGID = ? AND Preventive_Maintenance.ID = ?", AccountUtil.getCurrentOrg().getOrgId(), pmId);

		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		Map<String, Object> pmProp = pmProps.get(0);
		
		PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(pmProp, PreventiveMaintenance.class);
		pm.setTriggers(PreventiveMaintenanceAPI.getPMTriggers(pm));
		
		JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), pm.getTemplateId());
		JSONObject templateContent = template.getTemplate(new HashMap<String, Object>());
		JSONObject woContent = (JSONObject) templateContent.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		WorkOrderContext workorder = FieldUtil.getAsBeanFromJson(woContent, WorkOrderContext.class);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		
		JSONObject taskContent = (JSONObject) templateContent.get(FacilioConstants.ContextNames.TASK_MAP);
		if(taskContent != null) {
			Map<String, List<TaskContext>> tasks = FieldUtil.getAsBeanFromJson(taskContent, Map.class);
			context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		}
		else {
			JSONArray taskJson = (JSONArray) templateContent.get(FacilioConstants.ContextNames.TASK_LIST);
			if (taskJson != null) {
				List<TaskContext> tasks = FieldUtil.getAsBeanListFromJsonArray(taskJson, TaskContext.class);
				if(tasks != null && !tasks.isEmpty()) {
					Map<String, List<TaskContext>> taskMap = new HashMap<>();
					taskMap.put(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION, tasks);
					context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
				}
			}
		}
		return false;
	}
}
