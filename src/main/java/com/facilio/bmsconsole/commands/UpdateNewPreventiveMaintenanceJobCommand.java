package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.time.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UpdateNewPreventiveMaintenanceJobCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		PMJobsContext pmJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_JOB);
		// long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
		Long resourceId = (Long) context.get(FacilioConstants.ContextNames.PM_RESOURCE_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		List<FacilioField> fields = modBean.getAllFields(module.getName());

		SelectRecordsBuilder<WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<>();
		selectRecordsBuilder
				.beanClass(WorkOrderContext.class)
				.module(module)
				.select(fields)
				.andCustomWhere("WorkOrders.ID = ?", recordIds.get(0));
		List<Map<String, Object>> pmProps = selectRecordsBuilder.getAsProps();

		pmProps.get(0).put("assignedTo", resourceId);
		pmProps.get(0).put("createdTime", pmJob.getNextExecutionTime());
		pmProps.get(0).put("scheduledStart", pmJob.getNextExecutionTime());
		FacilioModule ticketModule = ModuleFactory.getTicketsModule();

		if(resourceId != -1) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ticketModule.getTableName())
					.fields(Arrays.asList(FieldFactory.getAsMap(fields).get("assignedTo"), FieldFactory.getAsMap(fields).get("scheduledStart")))
					.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ticketModule))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, ticketModule));
			updateBuilder.update(pmProps.get(0));
		}

		if (pmJob != null && pmJob.getNextExecutionTime() > 0) {
			GenericUpdateRecordBuilder updateTicketBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(Arrays.asList(FieldFactory.getAsMap(fields).get("createdTime")))
					.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), module))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, module));
			updateTicketBuilder.update(pmProps.get(0));

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ticketModule.getTableName())
					.fields(Arrays.asList(FieldFactory.getAsMap(fields).get("scheduledStart")))
					.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), ticketModule))
					.andCondition(CriteriaAPI.getIdCondition(recordIds, ticketModule));
			updateBuilder.update(pmProps.get(0));
		}
		return false;
	}
	
	private long updateWO(long templateId, long resourceId) throws Exception {
		Template template = TemplateAPI.getTemplate(templateId);
		long newTemplateId = -1;
		WorkOrderContext wo = ((WorkorderTemplate)template).getWorkorder();
		Map<String, List<TaskContext>> taskMap = ((WorkorderTemplate)template).getTasks();
		

		
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
