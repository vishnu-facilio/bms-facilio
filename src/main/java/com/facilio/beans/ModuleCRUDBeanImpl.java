package com.facilio.beans;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.timeseries.TimeSeriesAPI;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	@Override
	public long addWorkOrder(WorkOrderContext workorder) throws Exception {
		// TODO Auto-generated method stub
		if (workorder != null) {
			FacilioContext context = new FacilioContext();
			// context.put(FacilioConstants.ContextNames.TICKET,
			// workorder.getTicket());
			context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);

			Command addWorkOrder = FacilioChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(context);
			return workorder.getId();
		}
		return -1;
	}

	@Override
	public AlarmContext processAlarm(JSONObject alarmInfo) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);
		
		Chain addAlarmChain = FacilioChainFactory.getAddAlarmFromEventChain();
		addAlarmChain.execute(context);
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		return alarm;
	}

	@Override
	public int deleteAlarm(List<Long> id) throws Exception {
		// TODO Auto-generated method stub
		if (id != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

			Chain deleteAlarmChain = FacilioChainFactory.getDeleteAlarmChain();
			deleteAlarmChain.execute(context);

			int rowsDeleted = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			return rowsDeleted;
		}
		return 0;
	}

	@Override
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest, List<File> attachedFiles, List<String> attachedFileNames, List<String> attachedFilesContentType) throws Exception {
		// TODO Auto-generated method stub
		if (workOrderRequest != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrderRequest.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER_REQUEST, workOrderRequest);

			if (attachedFiles != null && !attachedFiles.isEmpty() && attachedFileNames != null && !attachedFileNames.isEmpty() && attachedFilesContentType != null && !attachedFilesContentType.isEmpty()) {
				context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, attachedFiles);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, attachedFileNames);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, attachedFilesContentType);
			}
			
			Command addWorkOrderRequest = FacilioChainFactory.getAddWorkOrderRequestChain();
			addWorkOrderRequest.execute(context);
			return workOrderRequest.getId();
		}
		return -1;
	}
	
	@Override
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception {
		// TODO Auto-generated method stub
		return addWorkOrderRequest(workOrderRequest, null, null, null);
	}

	@Override
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		if (alarm != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
			updateAlarm.execute(context);

			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return -1;
	}
	
	@Override
	public int updateAlarmFromJson(JSONObject alarmInfo, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

		Chain updateAlarm = FacilioChainFactory.updateAlarmFromJsonChain();
		updateAlarm.execute(context);

		return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
	}

	@Override
	public int updateAlarmPriority(String priority, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		if (priority != null && !priority.isEmpty()) {
			AlarmContext alarm = new AlarmContext();
			alarm.setPriority(TicketAPI.getPriority(AccountUtil.getCurrentOrg().getOrgId(), priority));

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
			updateAlarm.execute(context);
			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		}
		return -1;
	}

	@Override
	public int updateAlarmResource(long resourceId, String source) throws Exception {
		// TODO Auto-generated method stub
		if (source != null && !source.isEmpty() && resourceId != -1) {
			AlarmContext alarm = new AlarmContext();
			ResourceContext resource = new ResourceContext();
			resource.setId(resourceId);
			alarm.setResource(resource);

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ALARM, alarm);
			context.put(EventConstants.EventContextNames.SOURCE, source);

			Chain updateAlarm = FacilioChainFactory.getUpdateAlarmResourceChain();
			updateAlarm.execute(context);
			return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		}
		return -1;
	}
	
	@Override
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm) throws Exception {
		return addWorkOrderFromPM(pm, -1);
	}

	@Override
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm, long templateId) throws Exception {
		// TODO Auto-generated method stub
		if (pm != null) {
			if(templateId == -1) {
				templateId = pm.getTemplateId();
			}
			Template template = TemplateAPI.getTemplate(templateId);
			WorkOrderContext wo = null;
			Map<String, List<TaskContext>> taskMap = null;
			
			if (template instanceof JSONTemplate) {
				JSONObject content = template.getTemplate(null);
				JSONObject woJson = (JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER);
				
				wo = FieldUtil.getAsBeanFromJson(woJson, WorkOrderContext.class);
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
			
			wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
			wo.setPm(pm);
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
			context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
			context.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
			context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, wo.getAttachments());
			
			//Temp fix. Have to be removed eventually
			PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);
			Chain addWOChain = FacilioChainFactory.getAddWorkOrderChain();
			addWOChain.execute(context);

			incrementPMCount(pm);
			return wo;
		}
		return null;
	}
	
	private void incrementPMCount(PreventiveMaintenance pm) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException {
		PreventiveMaintenance updatePm = new PreventiveMaintenance();
		updatePm.setCurrentExecutionCount(updatePm.getCurrentExecutionCount());
		pm.setCurrentExecutionCount(pm.getCurrentExecutionCount()+1);
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(pm.getId(), module))
														;
		
		updateBuilder.update(FieldUtil.getAsProperties(updatePm));
	}
	
	public List<Map<String, Object>> CopyPlannedMaintenance() throws Exception
	{
		System.out.println("___________>>>>>>>>>>OrgID: "+AccountUtil.getCurrentOrg().getId());
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
		Map<String, FacilioField> pmFieldsMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getId());
						
	// selectBuilder.limit(1);													
	List<Map<String, Object>> props = selectBuilder.get();
	
	if(props != null && !props.isEmpty())
	{
	System.out.println("Number of Pm's in the Org:"+props.size());
	for (Map<String, Object> prop : props) {
		long templateId = (Long)prop.get("templateId");
		WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
		WorkorderTemplate woNewTemplate = new WorkorderTemplate();
		Long categoryId = (Long)prop.get("categoryId");
		if (categoryId != null)
		{
			prop.put("categoryName", TicketAPI.getCategory(AccountUtil.getCurrentOrg().getId(), categoryId).getName());
		}
		else {
			prop.put("categoryName", null);
		}
		woNewTemplate.setName(woTemplate.getName());
		woNewTemplate.setTasks(woTemplate.getTasks());
		prop.put("template", woNewTemplate);
		
		
		
		/*	Map<String, List<TaskContext>> taskMap = woTemplate.getTasks();
		
		Iterator itr = taskMap.keySet().iterator();
		while (itr.hasNext()) {
			String section = (String) itr.next();
			for (TaskContext tc : woTemplate.getTasks().get(section)) {
				System.out.println("Number of Pm in the Org: ["+section+"] "+ FieldUtil.getAsJSON(tc));
			}
		}*/
		
//		for(String key : prop.keySet()){
//			System.out.println(key + " : "+prop.get(key));
//			/*long templateId = (Long)prop.get("templateId");
//			WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
//			woTemplate.setOrgId(newOrgId);
//			TemplateAPI.addPMWorkOrderTemplate(woTemplate);
//		*/
//		}
	}
												
	return props;
	}
	return null;
	}
	
	@Override
	public PreventiveMaintenance CopyWritePlannedMaintenance(List<Map<String, Object>> props) throws Exception
	{
		
		System.out.println("___________>>>>>>>>>>New OrgID: "+AccountUtil.getCurrentOrg().getId());
		for (Map<String, Object> prop : props) {
			// System.out.println("$$$$$$$$$$woTemplate:"+prop.get("template"));
			
			WorkorderTemplate woTemplate = (WorkorderTemplate) prop.get("template");
			long orgId = AccountUtil.getCurrentOrg().getId();
			String category = (String) prop.get("categoryName");
			if (category != null)
			{
			
			TicketCategoryContext categoryId = TicketAPI.getCategory(orgId, category);
			
			if (categoryId != null)
			{
			// System.out.println("+++++++++++++++++" +FieldUtil.getAsJSON(woTemplate));
			 woTemplate.setCategoryId(categoryId.getId());
			}
			else
			{
				TicketCategoryContext record = new TicketCategoryContext();
				record.setName(category);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule("ticketcategory");
				
				InsertRecordBuilder<ModuleBaseWithCustomFields> insertRecordBuilder = new InsertRecordBuilder<ModuleBaseWithCustomFields>()
																							.module(module)
																							.fields(modBean.getAllFields("ticketcategory"));
				
				long id = insertRecordBuilder.insert(record);
				
				woTemplate.setCategoryId(id);
			}
			}
			woTemplate.setOrgId(orgId);
			long tempId = TemplateAPI.addPMWorkOrderTemplate(woTemplate);
			System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<" +tempId);
			
			PreventiveMaintenance newPm = new PreventiveMaintenance();
			
			newPm.setTitle((String) prop.get("title"));
			newPm.setTemplateId(tempId);
			newPm.setCreatedById(AccountUtil.getOrgBean().getSuperAdmin(orgId).getId());
			newPm.setOrgId(orgId);
			newPm.setCreatedTime(System.currentTimeMillis());
			newPm.setStatus(false);
			newPm.setTriggerType(4);
			
			Map<String, Object> pmProps = FieldUtil.getAsProperties(newPm);

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getPreventiveMaintenancetModule().getTableName())
					.fields(FieldFactory.getPreventiveMaintenanceFields()).addRecord(pmProps);
			builder.save();
			long id = (long) pmProps.get("id");
			
			System.out.println(">>>>>>>>>>>>>>>>>>>> PM ID: "+id);
//			for(String key : prop.keySet()){
////		long templateId = (Long)prop.get("template");
////		WorkorderTemplate woTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
//		
//			}
		}
		
		return null;
	}
	
	@Override
	public WorkOrderContext CloseAllWorkOrder() throws Exception
	{
		List<WorkOrderContext> workOrders = new ArrayList<WorkOrderContext>();
		List<TicketStatusContext> ticketStatus = new ArrayList<TicketStatusContext>();
		List<TaskContext> tasks = new ArrayList<TaskContext>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getId());
		FacilioModule module = modBean.getModule("workorder");
		 
		
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
				.table(module.getTableName())
				.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(WorkOrderContext.class)
				.andCriteria(ViewFactory.getAllOverdueWorkOrdersCriteria());
		
		workOrders = builder.get();
		
		System.out.println("___________>>>>>>>>>>Number of workorders OverDue: "+workOrders.size());
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(ModuleFactory.getTicketStatusIdField());
		
		SelectRecordsBuilder<TicketStatusContext> builder1 = new SelectRecordsBuilder<TicketStatusContext>()
				.table("TicketStatus")
				.moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
				.select(fields)
				.beanClass(TicketStatusContext.class)
				.andCustomWhere(ModuleFactory.getTicketStatusModule().getTableName()+".ORGID = ? AND STATUS = ?", AccountUtil.getCurrentOrg().getId(), "closed");
		
		ticketStatus = builder1.get();
		
		long tsid = ticketStatus.get(0).getId();
		
			
			
		System.out.println("@@@@@@@@@@@@@@@@@@@@@ Ticket Status Id"+ticketStatus.get(0).getId());
		
		for (WorkOrderContext workOrder : workOrders)
		{
			SelectRecordsBuilder<TaskContext> taskbuilder = new SelectRecordsBuilder<TaskContext>()
				.table("Tasks")
				.moduleName(FacilioConstants.ContextNames.TASK)
				.beanClass(TaskContext.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
				.andCustomWhere("Tasks.PARENT_TICKET_ID = ?", workOrder.getId());
		
		
		
			tasks = taskbuilder.get();
		
			// System.out.println("===============LLLLLLLLL FL "+modBean.getAllFields(FacilioConstants.ContextNames.TICKET));
			
			
			if (tasks != null && !tasks.isEmpty())
			{
				StringJoiner taskids = new StringJoiner(",");
				List<Long> taskIdList = new ArrayList<>();
				System.out.println("@@@@@@@@@@@@@@@@@@@@@Number of Tasks in the Workorder: "+tasks.size()+ "for Workorder"+workOrder.getId());
				String questMark = "";
				int idx = 0;
				for (TaskContext task : tasks) 
				{
					taskIdList.add(task.getId());
					if (idx == 0) {
						questMark += "?";
					}
					else {
						questMark += ", ?";
					}
					idx ++;
				}
				System.out.println("===============LLLLLLLLLTask ID's"+taskIdList);
				System.out.println("===============LLLLLLLLL"+questMark);
				
					UpdateRecordBuilder<TaskContext> updateTaskBuilder = new UpdateRecordBuilder<TaskContext>()
					.moduleName(FacilioConstants.ContextNames.TASK)
					.fields(modBean.getAllFields(FacilioConstants.ContextNames.TASK))
					.andCustomWhere(" Tickets.ID in ( "+questMark+" )", taskIdList.toArray());
					
			
					TaskContext ts = new TaskContext();
					ts.setStatus(ticketStatus.get(0));
			
					updateTaskBuilder.update(ts);
				
		
		}
		System.out.println("===============LLLLLLLLL WO"+workOrder.getId());
		
		
		
			UpdateRecordBuilder<WorkOrderContext> updateWOBuilder = new UpdateRecordBuilder<WorkOrderContext>()
				.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
				.andCustomWhere("Tickets.ID = ?", workOrder.getId());
		
			WorkOrderContext wo = new WorkOrderContext();
			wo.setStatus(ticketStatus.get(0));
		
			updateWOBuilder.update(wo);
		
		}
		
		return null;
		
	}
	
	@Override
	public void deleteAllData(String moduleName) throws Exception {
		// TODO Auto-generated method stub
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		FacilioModule extendedModule = module.getExtendModule();
		while(extendedModule != null) {
			module = extendedModule;
			extendedModule = extendedModule.getExtendModule();
		}
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(module), String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
														;
		deleteBuilder.delete();
	}

	@Override
	public void processTimeSeries(long timeStamp, JSONObject payLoad) throws Exception {
			TimeSeriesAPI.processPayLoad(timeStamp, payLoad);
	}

	@Override
	public long processEvents(long timeStamp, JSONObject payLoad, List<EventRuleContext> eventRules,
			Map<String, Integer> eventCountMap, long lastEventTime) throws Exception {
		return EventAPI.processEvents(timeStamp, payLoad, eventRules, eventCountMap, lastEventTime);
	}

	@Override
	public List<EventRuleContext> getActiveEventRules() throws Exception {
		return EventRulesAPI.getActiveEventRules();
	}

}
