package com.facilio.workflowv2.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.command.SchedulerAPI;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;

public class WorkflowV2API {
	
	public static ScheduledWorkflowContext getScheduledWorkflowContext(long scheduledWorkflowId,Boolean isActive) throws Exception {
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(FieldFactory.getScheduledWorkflowFields())
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowId, ModuleFactory.getScheduledWorkflowModule()));
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScheduledWorkflowFields());
		if(isActive != null) {
			select.andCondition(CriteriaAPI.getCondition(fieldMap.get("isActive"), isActive.toString(), BooleanOperators.IS));
		}
		
		List<Map<String, Object>> props = select.get();
		
		ScheduledWorkflowContext scheduledWorkflowContext = null;
		if(props != null && !props.isEmpty()) {
			scheduledWorkflowContext = FieldUtil.getAsBeanFromMap(props.get(0), ScheduledWorkflowContext.class);
			
			SchedulerAPI.getSchedulerActions(Collections.singletonList(scheduledWorkflowContext));
		}
		return scheduledWorkflowContext;
	}
	
	public static List<WorkflowNamespaceContext> getAllNameSpace() throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName());

		List<Map<String, Object>> props = selectBuilder.get();

		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, WorkflowNamespaceContext.class);
		}
		return null;
	}

	public static List<WorkflowUserFunctionContext> getAllFunctions() throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowUserFunctionModule();
		List<FacilioField> fields = FieldFactory.getWorkflowUserFunctionFields();
		fields.addAll(FieldFactory.getWorkflowFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.innerJoin(ModuleFactory.getWorkflowModule().getTableName())
				.on(ModuleFactory.getWorkflowModule().getTableName()+".ID="+module.getTableName()+".ID");

		List<Map<String, Object>> props = selectBuilder.get();

		
		List<WorkflowUserFunctionContext> functions = new ArrayList<WorkflowUserFunctionContext>(); 
		if (props != null && !props.isEmpty()) {
			 Map<Long, WorkflowNamespaceContext> namespaceMap = getAllNameSpace().stream().collect(Collectors.toMap(WorkflowNamespaceContext::getId, Function.identity()));
			
			for(Map<String, Object> prop :props) {
				WorkflowUserFunctionContext workflowContext = FieldUtil.getAsBeanFromMap(prop, WorkflowUserFunctionContext.class);
				
				WorkflowNamespaceContext nameSpace = namespaceMap.get(workflowContext.getNameSpaceId());
				workflowContext.setNameSpaceName(nameSpace.getName());
				
				functions.add(workflowContext);
			}
		}
		return functions;
	}
}
