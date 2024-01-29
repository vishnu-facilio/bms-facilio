package com.facilio.workflowv2.modulefunctions;

import java.util.*;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.JobPlanApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.util.FacilioUtil;

@ScriptModule(moduleName = FacilioConstants.ContextNames.WORK_ORDER)
public class FacilioWorkOrderModuleFunctions extends FacilioModuleFunctionImpl {

	private static final Logger LOGGER = LogManager.getLogger(FacilioWorkOrderModuleFunctions.class.getName());

	public List<Map<String, Object>> getAvgResolutionTime(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		return WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects.get(0).toString()), Long.valueOf(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()));
	}

	public List<Map<String, Object>> getWorkOrdersByCompletionTime(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		return WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects.get(0)), Long.valueOf(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()));
	}

	public List<Map<String, Object>> getTopNTechnicians(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		return WorkOrderAPI.getTopNTechnicians(objects.get(0).toString(), Long.valueOf(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()));
	}

	public List<Map<String, Object>> getTopNBuildings(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		return WorkOrderAPI.getTopNBuildings(Integer.parseInt(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()), Long.valueOf(objects.get(3).toString()), Long.valueOf(objects.get(4).toString()));
	}

	/**
	 * In script this function would look like,
	 * reschedulePreOpenWorkOrder(workOrderID, createdTime)
	 *
	 * createdTime updates modifiedTime(as createdTime), scheduledStart(as createdTime), estimatedStart(as createdTime)
	 * dueDuration updates dueDate(as createdTime + dueDuration), estimatedEnd(as createdTime + dueDuration) | dueDuration fetched from PMv2 directly
	 *
	 */
	public String reschedulePreOpenWorkOrder(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {

		String functionParamsMessage = " | This function takes in (workOrderId, createdTime) only";

		if(objects.size() < 3){
			if(objects.size() == 1){ // calling empty function
				throw new RuntimeException("WorkOrder ID and createdTime is required." + functionParamsMessage);
			}else if(objects.size() == 2){ // calling function with workOrderID
				throw new RuntimeException("CreatedTime is required." + functionParamsMessage);
			}else {
				throw new RuntimeException("Please check the parameters." + functionParamsMessage);
			}
		}else if(objects.size() > 3){
			throw new RuntimeException("Excess parameters aren't allowed." + functionParamsMessage);
		}

		//objects[moduleName, workOrderID, createdTime]
		Object workOrderId = objects.get(1);
		Object createdTime = objects.get(2);

		if ((workOrderId instanceof Long || workOrderId instanceof Integer) && createdTime  instanceof Long) {

			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> woFieldsList = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> woFieldsMap = FieldFactory.getAsMap(woFieldsList);

			SelectRecordsBuilder<V3WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<V3WorkOrderContext>()
					.module(woModule)
					.select(woFieldsList)
					.beanClass(V3WorkOrderContext.class)
					.andCondition(CriteriaAPI.getIdCondition((Long) workOrderId, woModule))
					.skipModuleCriteria();

			List<V3WorkOrderContext> workOrderContextList = selectRecordsBuilder.get();

			if(CollectionUtils.isEmpty(workOrderContextList)){
				return "No WorkOrder found with ID: " + workOrderId;
			}

			V3WorkOrderContext workOrderContext = workOrderContextList.get(0);

			// Check for dueDuration availability from PM_V2
			long dueDuration = 0L;
			if(workOrderContext.getPmV2() != null){
				PlannedMaintenance pmv2 = V3RecordAPI.getRecord(FacilioConstants.ContextNames.PLANNEDMAINTENANCE, workOrderContext.getPmV2());
				if(pmv2 != null && pmv2.getDueDuration() != null && pmv2.getDueDuration() > 0){
					dueDuration = pmv2.getDueDuration();
				}
			}

			List<FacilioField> fieldsToBeUpdate = new ArrayList<>();
			fieldsToBeUpdate.add(woFieldsMap.get("createdTime"));
			fieldsToBeUpdate.add(woFieldsMap.get("modifiedTime"));
			fieldsToBeUpdate.add(woFieldsMap.get("scheduledStart"));
			fieldsToBeUpdate.add(woFieldsMap.get("estimatedStart"));

			V3WorkOrderContext updateWorkOrderContext = new V3WorkOrderContext();
			updateWorkOrderContext.setCreatedTime((Long) createdTime);
			updateWorkOrderContext.setModifiedTime((Long) createdTime);
			updateWorkOrderContext.setScheduledStart((Long) createdTime);

			if(dueDuration > 0){
				fieldsToBeUpdate.add(woFieldsMap.get("dueDate"));
				fieldsToBeUpdate.add(woFieldsMap.get("estimatedEnd"));

				long dueDate = (Long) createdTime + (dueDuration * 1000);
				updateWorkOrderContext.setDueDate(dueDate);
				updateWorkOrderContext.setEstimatedEnd(dueDate);
			}

			UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<V3WorkOrderContext>()
					.module(woModule)
					.fields(fieldsToBeUpdate)
					.andCondition(CriteriaAPI.getIdCondition((Long) workOrderId, woModule))
					.skipModuleCriteria();
			int updated = updateRecordBuilder.update(updateWorkOrderContext);
			if(updated > 0) {
				return "Updated WorkOrder with ID: " + workOrderContext.getId();
			}else {
				return "Not Updated the WorkOrder with ID: " + workOrderContext.getId();
			}
		}

		throw new RuntimeException("Input not of expected type.");
	}

	/**
	 * In script this function would look like,
	 * updatePreOpenWorkOrderField(workOrderId, fieldName, value)
	 *
	 *
	 */
	public String updatePreOpenWorkOrderField(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		String functionParamsMessage = " | This function takes in (workOrderId, fieldName, value) only";

		if(objects.size() < 4){
			if(objects.size() == 1) { // calling empty function
				throw new RuntimeException("(workOrderId, fieldName, value parameters) are required." + functionParamsMessage);
			}
			throw new RuntimeException("Please check the parameters." + functionParamsMessage);
		}else if(objects.size() > 4){
			throw new RuntimeException("Excess parameters aren't allowed." + functionParamsMessage);
		}

		//objects[moduleName, workOrderId, fieldName, value]
		Object workOrderId = objects.get(1);
		Object fieldName = objects.get(2);
		Object value = objects.get(3);
		if ((workOrderId instanceof Long || workOrderId instanceof Integer) && fieldName instanceof String && value instanceof HashMap) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> woFieldsList = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> woFieldsMap = FieldFactory.getAsMap(woFieldsList);

			if (woFieldsMap.get(fieldName) == null){
				throw new RuntimeException("WorkOrder field doesn't exist." + fieldName);
			}

			SelectRecordsBuilder<V3WorkOrderContext> selectRecordsBuilder = new SelectRecordsBuilder<V3WorkOrderContext>()
					.module(woModule)
					.select(Collections.singleton(woFieldsMap.get("subject")))
					.beanClass(V3WorkOrderContext.class)
					.andCondition(CriteriaAPI.getIdCondition((Long) workOrderId, woModule))
					.skipModuleCriteria();

			List<V3WorkOrderContext> workOrderContextList = selectRecordsBuilder.get();

			if(CollectionUtils.isEmpty(workOrderContextList)){
				return "No WorkOrder found with ID: " + workOrderId;
			}

			V3WorkOrderContext workOrderContext = workOrderContextList.get(0);

			Map<String, Object>  valueMap= (HashMap) value;
 			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put((String) fieldName, valueMap.get(fieldName));

			V3WorkOrderContext updateWorkOrderContext = FieldUtil.getAsBeanFromMap(updateMap, V3WorkOrderContext.class);

			UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<V3WorkOrderContext>()
					.module(woModule)
					.fields(Collections.singletonList(woFieldsMap.get(fieldName)))
					.andCondition(CriteriaAPI.getIdCondition((Long) workOrderId, woModule))
					.skipModuleCriteria();
			int updated = updateRecordBuilder.update(updateWorkOrderContext);

			if(updated > 0) {
				return "Updated WorkOrder with ID: " + workOrderContext.getId();
			}else {
				return "Not Updated the WorkOrder with ID: " + workOrderContext.getId();
			}
		}
		throw new RuntimeException("Input not of expected type.");
	}

	/**
	 * In script this function would look like,
	 * updatePreOpenWorkOrders(workOrderIds, recordMap)
	 *
	 *
	 */
	public String updatePreOpenWorkOrders(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {
		String functionParamsMessage = " | This function takes in (workOrderIds, recordMap) only";

		if(objects.size() < 3){
			if(objects.size() == 1) { // calling empty function
				throw new RuntimeException("(workOrderIds, recordMap) are required." + functionParamsMessage);
			}
			throw new RuntimeException("Please check the parameters." + functionParamsMessage);
		}else if(objects.size() > 3){
			throw new RuntimeException("Excess parameters aren't allowed." + functionParamsMessage);
		}

		//objects[moduleName, workOrderIds, recordMap]
		Object workOrderIds = objects.get(1);
		Object recordMap = objects.get(2);
		if ((workOrderIds instanceof List) && recordMap instanceof HashMap) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
			List<FacilioField> woFieldsList = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
			Map<String, FacilioField> woFieldsMap = FieldFactory.getAsMap(woFieldsList);
			woFieldsMap.remove("serialNumber");
			woFieldsList = new ArrayList<>(woFieldsMap.values());

			List<Long> workOrderIdList = (List<Long>) workOrderIds;

			HashMap<String, Object> woRecordMap = (HashMap<String, Object>) recordMap;
			V3WorkOrderContext updateWorkOrderContext = FieldUtil.getAsBeanFromMap(woRecordMap, V3WorkOrderContext.class);

			FacilioModule ticketStatusModule = modBean.getModule(ModuleFactory.getTicketStatusModule().getName());
			List<FacilioField> ticketStatusFields = modBean.getAllFields(ModuleFactory.getTicketStatusModule().getName());
			Map<String, FacilioField> ticketStatusFieldMap = FieldFactory.getAsMap(ticketStatusFields);

			SelectRecordsBuilder<FacilioStatus> selectRecordsBuilder = new SelectRecordsBuilder<>();
			selectRecordsBuilder.module(ticketStatusModule)
					.select(ticketStatusFields)
					.beanClass(FacilioStatus.class)
					.andCondition(CriteriaAPI.getCondition(ticketStatusFieldMap.get("parentModuleId"),woModule.getModuleId() +"", NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(ticketStatusFieldMap.get("typeCode"), FacilioStatus.StatusType.PRE_OPEN.getIntVal()+"", NumberOperators.EQUALS));
			List<FacilioStatus> facilioStatuses = selectRecordsBuilder.get();

			if(CollectionUtils.isEmpty(facilioStatuses)){
				LOGGER.log(Priority.WARN, "Empty Ticket Status");
			}

			FacilioStatus preOpenStatus = null;
			for (FacilioStatus status: facilioStatuses){
				if(status.getType().equals(FacilioStatus.StatusType.PRE_OPEN) && status.getStatus().equals("preopen")){
					preOpenStatus = status;
				}
			}

			if(preOpenStatus == null){
				throw new RuntimeException("Unable to find preopen status.");
			}

			UpdateRecordBuilder<V3WorkOrderContext> updateRecordBuilder = new UpdateRecordBuilder<V3WorkOrderContext>()
					.module(woModule)
					.fields(woFieldsList)
					.andCondition(CriteriaAPI.getIdCondition(workOrderIdList, woModule))
					.andCondition(CriteriaAPI.getCondition(woFieldsMap.get("status"), preOpenStatus.getId()+"", NumberOperators.EQUALS))
					.skipModuleCriteria();
			int updated = updateRecordBuilder.update(updateWorkOrderContext);

			if(updated > 0) {
				return "Updated WorkOrder with IDs: " + workOrderIdList;
			}else {
				return "Not Updated the WorkOrder with IDs: " + workOrderIdList;
			}
		}
		throw new RuntimeException("Input not of expected type.");
	}


	@Override
	public void add(Map<String, Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception {

		Object insertObject = objects.get(1);

		if (insertObject instanceof Map) {
			Map<String, Object> woMap = (Map<String, Object>) insertObject;
			WorkOrderContext wo = FieldUtil.getAsBeanFromMap(woMap, WorkOrderContext.class);

			long id = addWorkOrderContext(wo);
			woMap.put("id", id);
			scriptContext.incrementTotalInsertCount();
		} else if (insertObject instanceof Collection) {

			List<Object> insertList = (List<Object>) insertObject;

			for (Object insert : insertList) {
				Map<String, Object> woMap = (Map<String, Object>) insert;
				WorkOrderContext wo = FieldUtil.getAsBeanFromMap(woMap, WorkOrderContext.class);
				long id = addWorkOrderContext(wo);
				woMap.put("id", id);
				scriptContext.incrementTotalInsertCount();
			}
		}

	}

	public long addWorkOrderContext(WorkOrderContext workorder) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule woModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		FacilioContext ctx = V3Util.createRecord(woModule, FieldUtil.getAsJSON(workorder));
		return ((V3WorkOrderContext) ctx.get("workorder")).getId();

	}
	
	public void addTask(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		Long woId = Long.parseLong(objects.get(1).toString());
		
		Map<String, List<TaskContext>> taskList = new HashMap<String, List<TaskContext>>(); 
		
		Map<String,List<Map<String,Object>>> taskMap = (Map<String, List<Map<String, Object>>>) objects.get(2);
		
		for(String section : taskMap.keySet()) {
			
			List<TaskContext> tasks = FieldUtil.getAsBeanListFromMapList(taskMap.get(section), TaskContext.class);
			for(TaskContext task :tasks) {
				task.setParentTicketId(woId);
			}
			taskList.put(section, tasks);
		}
		
		FacilioChain chain = TransactionChainFactory.getAddNewTasksChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.WORK_ORDER,WorkOrderAPI.getWorkOrder(woId));
		context.put(FacilioConstants.ContextNames.TASK_MAP,taskList);
		
		chain.execute();
		
	}
	
	@Override
	public Map<String, Object> addTemplateData(Map<String,Object> globalParams,List<Object> objects , ScriptContext scriptContext) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String templateRef = objects.get(1).toString();
		
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
		if(FacilioUtil.isNumeric(templateRef)) {
			
			context.put(FacilioConstants.ContextNames.FORM_ID, Double.valueOf(templateRef).longValue());
		}
		else {
			context.put(FacilioConstants.ContextNames.FORM_NAME, templateRef);
		}
		
		chain.execute();
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		List<FormField> fields = form.getFields();
		
		Map<String, Object> actualData = new HashMap<>(); 
		
		Map<String, List<TaskContext>> taskList = null; 
		
		for(FormField field :fields) {
			
			if(field.getValue() != null) {
				FacilioField facilioField = field.getField();
				
				if(facilioField != null) {
					if(facilioField.getName().equals("resource")) {
						actualData.put(field.getName(),field.getValue());
						continue;
					}
					else if(facilioField.getDataTypeEnum() == FieldType.LOOKUP) {
						JSONObject json = new JSONObject();
						json.put("id", field.getValue());
						actualData.put(field.getName(),json);
						continue;
					}
				}
				
				if(field.getName() != null && field.getName().equals("assignment")) {
					JSONObject assignmentJson = FacilioUtil.parseJson(field.getValue().toString());
					for(Object key : assignmentJson.keySet()) {
						if(assignmentJson.get(key) != null && !assignmentJson.get(key) .toString().contains("\"\"") ) {		// dummy check need to be removed
							actualData.put(key.toString(), assignmentJson.get(key));
						}
					}
					continue;
				}
				
				if(field.getName() != null && field.getName().equals("tasks") && field.getValue() != null) {
					JobPlanContext jobPlan = JobPlanApi.getJobPlan(Long.valueOf(field.getValue().toString()));
					taskList = TemplateAPI.getTasksFromTemplate(jobPlan);
					continue;
				}
				
				actualData.put(field.getName(), field.getValue());
			}
		}
		if(objects.size() > 2) {
			Map<String, Object> data = (Map<String,Object>)objects.get(2);
			for(String name : data.keySet()) {
				if(name.equals("tasks") && taskList != null) {
					Map<String,Object> taskUniqueIdMap = (Map<String,Object>) data.get("tasks");
					
					for(String sectionName : taskList.keySet()) {
						List<TaskContext> tasks = taskList.get(sectionName);
						
						for(TaskContext task : tasks) {
							if(taskUniqueIdMap.get(task.getUniqueId()+"") != null) {
								Map<String, Object> taskMap = (Map<String,Object>) taskUniqueIdMap.get(task.getUniqueId()+"");
								if(taskMap.get("resourceId") != null) {
									task.setResource(ResourceAPI.getResource((long)taskMap.get("resourceId")));
								}
								if(taskMap.get("subject") != null) {
									task.setSubject((String)taskMap.get("subject"));
								}
							}
						}
					}
				}
				else {
					actualData.put(name, data.get(name));
				}
			}
		}
		
		WorkOrderContext workorder = (WorkOrderContext) FieldUtil.getAsBeanFromMap(actualData, WorkOrderContext.class);
		
		workorder.setSourceType(com.facilio.bmsconsole.context.TicketContext.SourceType.WEB_ORDER);
		
		FacilioChain addWoDataChain = TransactionChainFactory.getAddWorkOrderChain();
		context = addWoDataChain.getContext();
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		
		if(taskList != null) {
			workorder.setTaskList(taskList);
			context.put(FacilioConstants.ContextNames.TASK_MAP, taskList);
		}

		context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.FORM_NAME, form.getName());
		context.put(FacilioConstants.ContextNames.FORM_OBJECT, workorder);
		context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
		
		addWoDataChain.execute();
		scriptContext.incrementTotalInsertCount();
		return workorder.getData();
	}
	
	public Map<String, Object> addTemplateDataInternal(Map<String,Object> globalParams,List<Object> objects, ScriptContext scriptContext) throws Exception {
		
		FacilioModule module = (FacilioModule) objects.get(0);
		
		String templateRef = objects.get(1).toString();
		
		FacilioChain chain = FacilioChainFactory.getFormMetaChain();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME,module.getName());
		if(FacilioUtil.isNumeric(templateRef)) {
			
			context.put(FacilioConstants.ContextNames.FORM_ID, Double.valueOf(templateRef).longValue());
		}
		else {
			context.put(FacilioConstants.ContextNames.FORM_NAME, templateRef);
		}
		
		chain.execute();
		
		FacilioForm form = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
		List<FormField> fields = form.getFields();
		
		Map<String, Object> actualData = new HashMap<>(); 
		
		Map<String, List<TaskContext>> taskList = null; 
		
		for(FormField field :fields) {
			
			if(field.getValue() != null) {
				FacilioField facilioField = field.getField();
				
				if(facilioField != null) {
					if(facilioField.getName().equals("resource")) {
						actualData.put(field.getName(),field.getValue());
						continue;
					}
					else if(facilioField.getDataTypeEnum() == FieldType.LOOKUP) {
						JSONObject json = new JSONObject();
						json.put("id", field.getValue());
						actualData.put(field.getName(),json);
						continue;
					}
				}
				
				if(field.getName() != null && field.getName().equals("assignment")) {
					JSONObject assignmentJson = FacilioUtil.parseJson(field.getValue().toString());
					for(Object key : assignmentJson.keySet()) {
						if(assignmentJson.get(key) != null && !assignmentJson.get(key) .toString().contains("\"\"") ) {		// dummy check need to be removed
							actualData.put(key.toString(), assignmentJson.get(key));
						}
					}
					continue;
				}
				
				if(field.getName() != null && field.getName().equals("tasks") && field.getValue() != null) {
					JobPlanContext jobPlan = JobPlanApi.getJobPlan(Long.valueOf(field.getValue().toString()));
					taskList = TemplateAPI.getTasksFromTemplate(jobPlan);
					continue;
				}
				
				actualData.put(field.getName(), field.getValue());
			}
		}
		if(objects.size() > 2) {
			Map<String, Object> data = (Map<String,Object>)objects.get(2);
			for(String name : data.keySet()) {
				if(name.equals("tasks") && taskList != null) {
					Map<String,Object> taskUniqueIdMap = (Map<String,Object>) data.get("tasks");
					
					for(String sectionName : taskList.keySet()) {
						List<TaskContext> tasks = taskList.get(sectionName);
						
						for(TaskContext task : tasks) {
							if(taskUniqueIdMap.get(task.getUniqueId()+"") != null) {
								Map<String, Object> taskMap = (Map<String,Object>) taskUniqueIdMap.get(task.getUniqueId()+"");
								if(taskMap.get("resourceId") != null) {
									task.setResource(ResourceAPI.getResource((long)taskMap.get("resourceId")));
								}
								if(taskMap.get("subject") != null) {
									task.setSubject((String)taskMap.get("subject"));
								}
							}
						}
					}
				}
				else {
					actualData.put(name, data.get(name));
				}
			}
		}
		
		FacilioChain addWOChain = TransactionChainFactory.getTempAddPreOpenedWorkOrderChain();
		
		context = addWOChain.getContext();
		
		WorkOrderContext workorder = (WorkOrderContext) FieldUtil.getAsBeanFromMap(actualData, WorkOrderContext.class);


		if (workorder.getSourceTypeEnum() == null) {
			workorder.setSourceType(com.facilio.bmsconsole.context.TicketContext.SourceType.WEB_ORDER);
		}
		
		if(taskList != null) {
			workorder.setTaskList(taskList);
//			context.put(FacilioConstants.ContextNames.TASK_MAP, taskList);
		}
		
		
		BulkWorkOrderContext bulkWorkOrderContext = new BulkWorkOrderContext();
		bulkWorkOrderContext.addContexts(workorder, taskList, null, null);
		context.put(FacilioConstants.ContextNames.BULK_WORK_ORDER_CONTEXT, bulkWorkOrderContext);
		context.put(FacilioConstants.ContextNames.ATTACHMENT_MODULE_NAME, FacilioConstants.ContextNames.TICKET_ATTACHMENTS);

		addWOChain.execute();
		scriptContext.incrementTotalInsertCount();
		return workorder.getData();
	}
	
}