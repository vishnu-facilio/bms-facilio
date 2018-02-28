package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdatePreventiveMaintenanceJobCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		PMJobsContext pmJobs = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_JOB);
		long pmId = (Long) context.get(FacilioConstants.ContextNames.PM_ID);
		long resourceId = (Long) context.get(FacilioConstants.ContextNames.PM_RESOURCE_ID);
		Map<String, Object> props = FieldUtil.getAsProperties(pmJobs);
		
		String ids = StringUtils.join(recordIds, ",");
		Condition idCondition = new Condition();
		idCondition.setField(FieldFactory.getIdField(ModuleFactory.getPMJobsModule()));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getPMJobsModule().getTableName())
				.fields(FieldFactory.getPMJobFields())
				.andCondition(idCondition);
		
		if(resourceId != -1)
		{
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
			
			props.put("templateId", newTemplateId);
		}
		
		updateBuilder.update(props);
		return false;
	}
	
	private long addWOTemplate(long templateId, long resourceId) throws Exception {
		Template template = TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), templateId);
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
		newTemplateId = TemplateAPI.addPMWorkOrderTemplate(AccountUtil.getCurrentOrg().getId(), woTemplate);
		return newTemplateId;
	}
}
