package com.facilio.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.agent.AgentKeys;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TicketCategoryContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.timeseries.TimeSeriesAPI;

public class ModuleCRUDBeanImpl implements ModuleCRUDBean {

	private static final Logger LOGGER = LogManager.getLogger(ModuleCRUDBeanImpl.class.getName());
	
	@Override
	public AlarmContext processAlarm(JSONObject alarmInfo) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);
		
		Chain addAlarmChain = TransactionChainFactory.getAddAlarmFromEventChain();
		addAlarmChain.execute(context);
		AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
		return alarm;
	}

	@Override
	public int deleteAlarm(List<Long> id) throws Exception {
		// TODO Auto-generated method stub
		if (id != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.DELETE);
			context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

			Chain deleteAlarmChain = FacilioChainFactory.getDeleteAlarmChain();
			deleteAlarmChain.execute(context);

			int rowsDeleted = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
			return rowsDeleted;
		}
		return 0;
	}

	public ControllerContext getController(String deviceName, String deviceId) throws Exception{
		if(deviceId != null && deviceName != null) {
			return ControllerAPI.getController(deviceName, deviceId);
		}
		return null;
	}

    public ControllerContext addController(ControllerContext controller) throws Exception{
        if(controller != null) {
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.CONTROLLER_SETTINGS, controller);
            Chain addcontrollerSettings = FacilioChainFactory.getAddControllerChain();
            addcontrollerSettings.execute(context);
        }
        return controller;
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
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			}
			
			Command addWorkOrderRequest = FacilioChainFactory.getAddWorkOrderRequestChain();
			addWorkOrderRequest.execute(context);
			return workOrderRequest.getId();
		}
		return -1;
	}
	
	@Override
	public long addWorkOrderFromEmail(WorkOrderContext workOrder, List<File> attachedFiles, List<String> attachedFileNames,
			List<String> attachedFilesContentType) throws Exception {
		if (workOrder != null) {
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.REQUESTER, workOrder.getRequester());
			context.put(FacilioConstants.ApprovalRule.APPROVAL_REQUESTER, workOrder.getRequester());
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workOrder);
			context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
			
			TicketStatusContext preOpenStatus = TicketAPI.getStatus("preopen");
			workOrder.setStatus(preOpenStatus);
			
			if (attachedFiles != null && !attachedFiles.isEmpty() && attachedFileNames != null && !attachedFileNames.isEmpty() && attachedFilesContentType != null && !attachedFilesContentType.isEmpty()) {
				context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, attachedFiles);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, attachedFileNames);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, attachedFilesContentType);
		 		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
			}
			
			Command addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
			addWorkOrder.execute(context);
			
			return workOrder.getId();
		}
		return -1;
	}


	@Override
	public int updateAlarmFromJson(JSONObject alarmInfo, List<Long> ids) throws Exception {
		// TODO Auto-generated method stub
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarmInfo);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		
		Chain updateAlarm = TransactionChainFactory.updateAlarmFromJsonChain();
		updateAlarm.execute(context);

		return (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
	}

	@Override
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm) throws Exception {
		return addWorkOrderFromPM(context, pm, -1);
	}

	@Override
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm, long templateId) throws Exception {
		// TODO Auto-generated method stub
		if (pm != null) {
			
			List<WorkOrderContext> workOrderContexts = new ArrayList<>();
			
			if(templateId == -1) {
				templateId = pm.getTemplateId();
			}
			Map<Long, PMResourcePlannerContext> resourcePlanners = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
			WorkorderTemplate workorderTemplate = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
			WorkOrderContext wo = null;
			List<Long> resourceIds = null;
			
			if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
				Long resourceId = pm.getBaseSpaceId();
				if (resourceId == null || resourceId < 0) {
					resourceId = pm.getSiteId();
				}
				resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),resourceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
			}
			else {
				resourceIds = Collections.singletonList(workorderTemplate.getResourceIdVal());
			}
			
			for(Long resourceId :resourceIds) {
				
				Map<String, List<TaskContext>> taskMap = null;
				
				if(resourcePlanners != null && resourcePlanners.containsKey(resourceId)) {
					PMResourcePlannerContext resourcePlanner = resourcePlanners.get(resourceId);
					if (resourcePlanner.getAssignedToId() != null && resourcePlanner.getAssignedToId() > 0 ) {
						workorderTemplate.setAssignedToId(resourcePlanner.getAssignedToId());
					}
				}
				
				wo = workorderTemplate.getWorkorder();
				wo.setResource(ResourceAPI.getResource(resourceId));
				taskMap = workorderTemplate.getTasks();

				Map<String, List<TaskContext>> taskMapForNewPmExecution = null;	// should be handled in above if too
				
				boolean isNewPmType = false;
				
				if(workorderTemplate.getSectionTemplates() != null) {
					for(TaskSectionTemplate sectiontemplate : workorderTemplate.getSectionTemplates()) {// for new pm_Type section should be present and every section should have a AssignmentType
						if(sectiontemplate.getAssignmentType() < 0) {
							isNewPmType =  false;
							break;
						} else {
							isNewPmType = true; 
						}
					}
				}

				PMTriggerContext pmTrigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER);
				wo.setTrigger(pmTrigger);

				if(isNewPmType) {
					Long woTemplateResourceId = wo.getResource() != null ? wo.getResource().getId() : -1;
					if(woTemplateResourceId > 0) {
						Long currentTriggerId = null;
						if (pmTrigger != null) {
							currentTriggerId = pmTrigger.getId();
						}
						taskMapForNewPmExecution = PreventiveMaintenanceAPI.getTaskMapForNewPMExecution(workorderTemplate.getSectionTemplates(), woTemplateResourceId, currentTriggerId);
					}
				} else {
					taskMapForNewPmExecution = workorderTemplate.getTasks();
				}
				if(taskMapForNewPmExecution != null) {
					taskMap = taskMapForNewPmExecution;
				}

				wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
				wo.setPm(pm);
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
				context.put(FacilioConstants.ContextNames.REQUESTER, wo.getRequester());
				context.put(FacilioConstants.ContextNames.TASK_MAP, taskMap);
				context.put(FacilioConstants.ContextNames.IS_PM_EXECUTION, true);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);
				context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTEXT_LIST, wo.getAttachments());
				
				//Temp fix. Have to be removed eventually
				PreventiveMaintenanceAPI.updateResourceDetails(wo, taskMap);
				Chain addWOChain = TransactionChainFactory.getAddWorkOrderChain();
				addWOChain.execute(context);
				
				// if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.SINGLE) { //Need to be handled for multiple resources, it causes deadlock
					// incrementPMCount(pm);
				// }
				workOrderContexts.add(wo);
			}
			return workOrderContexts;
		}
		return null;
	}
	
	private void incrementPMCount(PreventiveMaintenance pm) throws Exception {
//		if (AccountUtil.getCurrentOrg().getId() == 155 || AccountUtil.getCurrentOrg().getId() == 151 || AccountUtil.getCurrentOrg().getId() == 92) {
//			TransactionManager tm = FTransactionManager.getTransactionManager();
//			if (tm != null) {
//				Transaction t = tm.getTransaction();
//				LOGGER.info("Connection & free connection size before incrementing PM count : "+((FacilioTransaction) t).getConnectionSize()+"::"+((FacilioTransaction) t).getFreeConnectionSize());
//			}
//		}
		
		pm.setCurrentExecutionCount(pm.getCurrentExecutionCount()+1);
		Map<String, Object> props = new HashMap<>();
		props.put("currentExecutionCount", pm.getCurrentExecutionCount());
		
		if (pm.getCurrentExecutionCount() == pm.getMaxCount()) {
			PreventiveMaintenanceAPI.setPMInActive(pm.getId());
			props.put("status", false);
		}
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
														.fields(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(pm.getId(), module))
														;
		
		updateBuilder.update(props);
	}
	
	public List<Map<String, Object>> CopyPlannedMaintenance() throws Exception
	{
		System.out.println("___________>>>>>>>>>>OrgID: "+AccountUtil.getCurrentOrg().getId());
		
		FacilioModule module = ModuleFactory.getPreventiveMaintenanceModule();
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		
		FieldFactory.getAsMap(fields);
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
					.table(ModuleFactory.getPreventiveMaintenanceModule().getTableName())
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
		
		ticketStatus.get(0).getId();
		
			
			
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
				new StringJoiner(",");
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
					ts.setStatusNew(TaskStatus.CLOSED);
			
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
	public void processTimeSeries(long timeStamp, JSONObject payLoad, Record record,
			IRecordProcessorCheckpointer checkPointer, boolean adjustTime) throws Exception {
			TimeSeriesAPI.processPayLoad(timeStamp, payLoad, record, checkPointer, null, adjustTime);
	}
	
	@Override
	public void processTimeSeries(FacilioConsumer consumer, FacilioRecord record) throws Exception {
        TimeSeriesAPI.processFacilioRecord(consumer, record);
	}

	@Override
	public long processEvents(long timeStamp, JSONObject payLoad, List<EventRuleContext> eventRules,
			Map<String, Integer> eventCountMap, long lastEventTime, String partitionKey) throws Exception {
		if (partitionKey != null && !partitionKey.isEmpty()) {
			ControllerContext controller = ControllerAPI.getActiveController(partitionKey);
			long siteId = -1l;
			if (controller != null) {
				siteId = controller.getSiteId(); 
				payLoad.put("controllerId", controller.getId());
			}
			if (siteId != -1) {
				payLoad.put("siteId", siteId);
			}
			
		}
		
		return EventAPI.processEvents(timeStamp, payLoad, eventRules, eventCountMap, lastEventTime);
	}

	@Override
	public List<EventRuleContext> getActiveEventRules() throws Exception {
		return EventRulesAPI.getActiveEventRules();
	}

	@Override
	public Boolean isFeatureEnabled(FeatureLicense license) throws Exception {
		return AccountUtil.isFeatureEnabled(license);
	}

	@Override
	public int acknowledgePublishedMessage(long id) throws Exception {
		// TODO Auto-generated method stub
		return IoTMessageAPI.acknowdledgeMessage(id);
	}

	@Override
	public long addDeviceId(String deviceId) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(FieldFactory.getDeviceDetailsFields());
		HashMap<String, Object> device = new HashMap<>();
		device.put("deviceId", deviceId);
		device.put("inUse", true);
		device.put("lastUpdatedTime", System.currentTimeMillis());
		device.put("lastAlertedTime", 0L);
		device.put("alertFrequency", 2400000L);
		long id = builder.insert(device);
		return id;
	}

	@Override
	public Map<String, Long> getDeviceMap() throws Exception {
		// TODO Auto-generated method stub
		FacilioModule deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName()).andCondition(CriteriaAPI.getCurrentOrgIdCondition(deviceDetailsModule)).select(FieldFactory.getDeviceDetailsFields());
		HashMap<String, Long> deviceData = new HashMap<>();
		List<Map<String, Object>> data = builder.get();
		for(Map<String, Object> obj : data) {
			String deviceId = (String)obj.get("deviceId");
			Long id = (Long)obj.get("id");
			deviceData.put(deviceId, id);
		}
		return deviceData;
	}

	public  List<Map<String,Object>> getAgentDataMap(String agentName) throws Exception{
		FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(AgentKeys.AGENT_TABLE).select(FieldFactory.getAgentDataFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(agentDataModule))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(agentDataModule),"NULL", CommonOperators.IS_EMPTY));
		if (agentName != null)
		{
			return genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(agentDataModule),agentName,StringOperators.IS)).get();
		}
		else {
			 return genericSelectRecordBuilder.get();
			}

	}

	@Override
	public Long addLog(Map<String, Object> logData) throws Exception{
		FacilioModule logModule = ModuleFactory.getAgentLogModule();
		GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
															.table(AgentKeys.AGENT_LOG_TABLE)
															.fields(FieldFactory.getAgentLogFields());
		return genericInsertRecordBuilder.insert(logData);
	}

    @Override
    public void updateAgentMetrics(Map<String,Object> metrics) throws Exception {
		FacilioModule metricsmodule = ModuleFactory.getAgentMetricsModule();
		GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
																.table(AgentKeys.METRICS_TABLE)
																.fields(FieldFactory.getAgentMetricsFields())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(metricsmodule))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsmodule),metrics.get(AgentKeys.AGENT_ID).toString(),NumberOperators.EQUALS))
																.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(metricsmodule),metrics.get(EventUtil.DATA_TYPE).toString(),NumberOperators.EQUALS));

                genericUpdateRecordBuilder.update(metrics);


    }
    public void insertAgentMetrics(Map<String,Object> metrics)throws Exception{
		GenericInsertRecordBuilder genericInsertRecordBuilder = new GenericInsertRecordBuilder()
				.table(AgentKeys.METRICS_TABLE)
				.fields(FieldFactory.getAgentMetricsFields());
		genericInsertRecordBuilder.insert( metrics);
	}

    @Override
    public List<Map<String, Object>> getMetrics(Long agentId,Integer publishType, Long createdTime) throws Exception {
		FacilioModule metricsModule = ModuleFactory.getAgentMetricsModule();
		if(agentId != null && publishType != null) {
			GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
					.table(AgentKeys.METRICS_TABLE)
					.select(FieldFactory.getAgentMetricsFields())
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(metricsModule), agentId.toString(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(metricsModule), publishType.toString(), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(metricsModule))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getCreatedTime(metricsModule),createdTime.toString(),NumberOperators.EQUALS));
			return genericSelectRecordBuilder.get();
		}
		return new ArrayList<>();
    }

	/*@Override
	public List<Map<String, Object>> getIntegration() throws Exception {
		FacilioModule integrationModule = ModuleFactory.getIntegrationModule();
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(WattsenseKeys.INTEGRATION_TABLE_NAME)
				.select(FieldFactory.getIntegrationFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(integrationModule));
		return genericSelectRecordBuilder.get();
	}*/



}
