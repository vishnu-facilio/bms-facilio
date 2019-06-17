package com.facilio.workflows.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class GetAllNameSpaceWithFunctionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getWorkflowNamespaceFields())
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getWorkflowNamespaceModule()));

		List<Map<String, Object>> props = selectBuilder.get();
		
		List<WorkflowNamespaceContext> workflowNamespaceContexts = new ArrayList<>();
		
		List<Long> nameSpaceIds = new ArrayList<>();
		
		if(props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				WorkflowNamespaceContext workflowNamespaceContext = FieldUtil.getAsBeanFromMap(prop, WorkflowNamespaceContext.class);
				nameSpaceIds.add(workflowNamespaceContext.getId());
				workflowNamespaceContexts.add(workflowNamespaceContext);
			}
		}
		
		Map<Long,List<WorkflowUserFunctionContext>> functionMap = getAllFunctions(nameSpaceIds);
		
		for(WorkflowNamespaceContext workflowNamespaceContext :workflowNamespaceContexts) {
			
			List<WorkflowUserFunctionContext> functions = functionMap.get(workflowNamespaceContext.getId());
			workflowNamespaceContext.setFunctions(functions);
		}
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT_LIST, workflowNamespaceContexts);
		
		return false;
	}

	Map<Long,List<WorkflowUserFunctionContext>> getAllFunctions(List<Long> nameSpaceIds) throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), StringUtils.join(nameSpaceIds, ","),NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		Map<Long,List<WorkflowUserFunctionContext>> nameSpaceMap = new HashMap<>(); 
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop :props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				
				workflowContext.fillFunctionHeaderFromScript();
				
				List<WorkflowUserFunctionContext> workflowUserFunctionContexts = nameSpaceMap.get(workflowContext.getNameSpaceId());
				
				workflowUserFunctionContexts = workflowUserFunctionContexts == null ? new ArrayList<>() : workflowUserFunctionContexts;
				
				workflowUserFunctionContexts.add(workflowContext);
				
				nameSpaceMap.put(workflowContext.getNameSpaceId(), workflowUserFunctionContexts);
				
			}
		}
		return nameSpaceMap;
	}
}
