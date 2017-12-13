package com.facilio.bmsconsole.jobs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class PMToWorkOrder extends FacilioJob {

	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long pmId = jc.getJobId();
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getPreventiveMaintenanceFields())
															.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
															.andCustomWhere("ID = ?", pmId)
															;
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if(props != null && !props.isEmpty()) {
				Map<String, Object> prop = props.get(0);
				long templateId = (long) prop.get("templateId");
				JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), templateId);
				
				JSONObject content = template.getTemplate(null);
				WorkOrderContext wo = FieldUtil.getAsBeanFromJson((JSONObject)content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
				wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				
				JSONArray taskJson = (JSONArray) content.get(FacilioConstants.ContextNames.TASK_LIST);
				if(taskJson != null) {
					List<TaskContext> tasks = FieldUtil.getAsBeanListFromJsonArray(taskJson, TaskContext.class);
					context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
				}
				
				Chain addWOChain = FacilioChainFactory.getAddWorkOrderChain();
				addWOChain.execute(context);
				
				addToRelTable(pmId, wo.getId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addToRelTable(long pmId, long woId) throws SQLException, RuntimeException {
		Map<String, Object> relProp = new HashMap<>();
		relProp.put("pmId", pmId);
		relProp.put("woId", woId);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getPmToWoRelFields())
														.table(ModuleFactory.getPmToWoRelModule().getTableName())
														.addRecord(relProp);
		
		insertBuilder.save();
	}

}
