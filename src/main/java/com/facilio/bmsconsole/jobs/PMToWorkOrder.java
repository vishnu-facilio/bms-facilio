package com.facilio.bmsconsole.jobs;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.WorkorderTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
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
				WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(OrgInfo.getCurrentOrgInfo().getOrgid(), templateId);
				
				JSONObject content = template.getTemplate(null);
				WorkOrderContext wo = FieldUtil.getAsBeanFromJson((JSONObject)content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
				
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				
				Chain addWOChain = FacilioChainFactory.getAddWorkOrderChain();
				addWOChain.execute(context);
				
				JSONArray taskJson = (JSONArray) content.get(FacilioConstants.ContextNames.TASK_LIST);
				if(taskJson != null) {
					List<TaskContext> tasks = FieldUtil.getAsBeanListFromJsonArray(taskJson, TaskContext.class);
					for(TaskContext task : tasks) {
						task.setParentTicketId(wo.getId());
					}
					
					context = new FacilioContext();
					context.put(FacilioConstants.ContextNames.TASK_LIST, tasks);
					
					Chain addTasksChain = FacilioChainFactory.getAddTasksChain();
					addTasksChain.execute(context);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
