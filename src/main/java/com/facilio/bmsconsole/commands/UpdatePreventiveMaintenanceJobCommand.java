package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;

public class UpdatePreventiveMaintenanceJobCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_JOB);
		long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
		long resourceId = (Long) context.get(FacilioConstants.ContextNames.PM_RESOURCE_ID);
		
		if(resourceId != -1) {
			List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
			FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
																.select(fields)
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCustomWhere("Preventive_Maintenance.ID = ?", pmId);
																;
			List<Map<String, Object>> pmProps = selectRecordBuilder.get();
			
			long templateId = (long) pmProps.get(0).get("templateId");
			long newTemplateId = addWOTemplate(templateId, resourceId);
			
			pmJob.setTemplateId(newTemplateId);
		}
		
		FacilioModule pmModule = ModuleFactory.getPMJobsModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(pmModule.getTableName())
				.fields(FieldFactory.getPMJobFields())
				.andCondition(CriteriaAPI.getIdCondition(recordIds, pmModule));
		updateBuilder.update(FieldUtil.getAsProperties(pmJob));
		
		if (pmJob.getStatusEnum() == PMJobsStatus.ACTIVE) {
			reScheduleIfRequired(recordIds);
		}
		
		return false;
	}
	
	private long addWOTemplate(long templateId, long resourceId) throws Exception {
		Template template = TemplateAPI.getTemplate(templateId);
		long newTemplateId = -1;
		WorkOrderContext wo = null;
		Map<String, List<TaskContext>> taskMap = null;
		if (template instanceof JSONTemplate) {
			JSONObject content = template.getTemplate(null);
			JSONObject woJson = (JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER);
			
			wo = FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class);
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			FacilioContext context = new FacilioContext();
			
			JSONObject taskContent = (JSONObject) content.get(FacilioConstants.ContextNames.TASK_MAP);
			if(taskContent != null) {
				taskMap = PreventiveMaintenanceAPI.getTaskMapFromJson(taskContent);
			}
			else {
				JSONArray taskJson = (JSONArray) content.get(FacilioConstants.ContextNames.TASK_LIST);
				if (taskJson != null) {
					List<TaskContext> tasks = FieldUtil.getAsBeanListFromJsonArray(taskJson, TaskContext.class);
					if(tasks != null && !tasks.isEmpty()) {
						taskMap = new HashMap<>();
						taskMap.put(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION, tasks);
					}
				}
			}
		}
		else {
			wo = ((WorkorderTemplate)template).getWorkorder();
			taskMap = ((WorkorderTemplate)template).getTasks();
		}
		
		User user = new User();
		user.setId(resourceId);
		wo.setAssignedTo(user);
		
		WorkorderTemplate woTemplate = new WorkorderTemplate();
		woTemplate.setWorkorder(wo);
		woTemplate.setTasks(taskMap);
		newTemplateId = TemplateAPI.addPMWorkOrderTemplate(woTemplate);
		return newTemplateId;
	}
	
	private void reScheduleIfRequired(List<Long> ids) throws Exception {
		List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getPMJobs(ids);
		long currentTime = DateTimeUtil.getCurrenTime(true);
		for (PMJobsContext pmJob : pmJobs) {
			if (pmJob.getNextExecutionTime() > currentTime) {
				PMJobsContext nextJob = PreventiveMaintenanceAPI.getNextPMJob(pmJob.getPmTriggerId(), pmJob.getNextExecutionTime(), true);
				if (nextJob.getStatusEnum() == PMJobsStatus.SCHEDULED) {
					FacilioTimer.deleteJob(nextJob.getId(), "PreventiveMaintenance");
					PreventiveMaintenanceAPI.updatePMJobStatus(nextJob.getId(), PMJobsStatus.ACTIVE);
					PreventiveMaintenanceAPI.schedulePMJob(pmJob);
				}
			}
		}
	}
}
