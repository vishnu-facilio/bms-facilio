package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

;

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
			FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
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
		WorkOrderContext wo = ((WorkorderTemplate)template).getWorkorder();
		Map<String, List<TaskContext>> taskMap = ((WorkorderTemplate)template).getTasks();
		
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
		if (pmJobs != null && !pmJobs.isEmpty()) {
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
}
