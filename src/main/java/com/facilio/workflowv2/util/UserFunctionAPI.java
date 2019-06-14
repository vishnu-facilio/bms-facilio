package com.facilio.workflowv2.util;

import java.util.List;
import java.util.Map;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
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

public class UserFunctionAPI {
	
	public static WorkflowNamespaceContext getNameSpace(String name) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowNamespaceContext workflowNamespaceContext = null;
		if (props != null && !props.isEmpty()) {
			workflowNamespaceContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowNamespaceContext.class);
		}
		return workflowNamespaceContext;
	}

	public static WorkflowContext getWorkflowFunction(String nameSpace, String functionName) throws Exception {

		WorkflowNamespaceContext workflowNamespaceContext = getNameSpace(nameSpace);
		return getWorkflowFunction(workflowNamespaceContext.getId(), functionName);
	}

	public static WorkflowContext getWorkflowFunction(Long nameSpaceId, String functionName) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), functionName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), nameSpaceId + "",
						NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowContext.class);
		}
		return workflowContext;
	}
	
}
