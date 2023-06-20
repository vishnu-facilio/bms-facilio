package com.facilio.workflowv2.modulefunctions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.fw.BeanFactory;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.v3.util.V3Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BulkWorkOrderContext;
import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.context.NoteContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
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