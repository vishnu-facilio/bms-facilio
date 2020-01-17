package com.facilio.workflowv2.modulefunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext.SourceType;
import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.JobPlanApi;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.WorkOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;

public class FacilioWorkOrderModuleFunctions extends FacilioModuleFunctionImpl {

	public List<Map<String,Object>> getAvgResolutionTime(List<Object> objects) throws Exception {
		return WorkOrderAPI.getTopNCategoryOnAvgCompletionTime(String.valueOf(objects.get(0).toString()),Long.valueOf(objects.get(1).toString()),Long.valueOf(objects.get(2).toString()));
	}
	
	public List<Map<String,Object>> getWorkOrdersByCompletionTime(List<Object> objects) throws Exception {
		return WorkOrderAPI.getWorkOrderStatusPercentageForWorkflow(String.valueOf(objects.get(0)),Long.valueOf(objects.get(1).toString()),Long.valueOf(objects.get(2).toString()));
	}
	
	public List<Map<String, Object>> getTopNTechnicians(List<Object> objects) throws Exception{
		return WorkOrderAPI.getTopNTechnicians(objects.get(0).toString(), Long.valueOf(objects.get(1).toString()), Long.valueOf(objects.get(2).toString()));
	}
	
	@Override
	public void add(List<Object> objects) throws Exception {

		Object insertObject = objects.get(1);
		
		List<WorkOrderContext> workorders = new ArrayList<WorkOrderContext>();
		if(insertObject instanceof Map) {

			WorkOrderContext wo = FieldUtil.getAsBeanFromMap((Map<String, Object>) insertObject, WorkOrderContext.class);
			
			workorders.add(wo);
		}
		else if (insertObject instanceof Collection) {
			
			List<Object> insertList = (List<Object>)insertObject;
			
			for(Object insert :insertList) {
				WorkOrderContext wo = FieldUtil.getAsBeanFromMap((Map<String, Object>) insert, WorkOrderContext.class);
				workorders.add(wo);
			}
		}
		
		for(WorkOrderContext workorder :workorders) {
			
			if (workorder.getSourceTypeEnum() == null) {
				workorder.setSourceType(TicketContext.SourceType.WEB_ORDER);
			}
			
			FacilioChain addWorkOrder = TransactionChainFactory.getAddWorkOrderChain();
			FacilioContext context = addWorkOrder.getContext();

			context.put(FacilioConstants.ContextNames.REQUESTER, workorder.getRequester());
			if (AccountUtil.getCurrentUser() == null) {
				context.put(FacilioConstants.ContextNames.IS_PUBLIC_REQUEST, true);
			}
			
			context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
			
			context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);

			addWorkOrder.execute();
		}
		
	}
	
	@Override
	public Map<String, Object> addTemplateData(List<Object> objects) throws Exception {
		
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
				actualData.put(name, data.get(name));
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
		
		return workorder.getData();
	}
	
}