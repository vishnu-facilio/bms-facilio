package com.facilio.workflowv2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.Visitor.FacilioWorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.DataTypeSpecificFunctionContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;

public class WorkflowV2Util {
	
	
	public static String getModuleClassNameFromModuleName(String moduleName) {
		return "com.facilio.workflowv2.modulefunctions.Facilio"+moduleName+"ModuleFunctions";
	}

	public static void fillExtraInfo(Value paramValue,FacilioModule module,ModuleBean modBean) throws Exception {
		
		if(paramValue.asObject() instanceof DBParamContext) {
			DBParamContext dbParamContext = (DBParamContext)paramValue.asObject();
			for(String key :dbParamContext.getCriteria().getConditions().keySet()) {
				Condition condition = dbParamContext.getCriteria().getConditions().get(key);
				FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
				condition.setField(field);
			}
		}
		
	}
	
	public static List<Object> getParamList(DataTypeSpecificFunctionContext ctx, boolean isDataTypeSpecificFunction, FacilioWorkflowFunctionVisitor facilioWorkflowFunctionVisitor, Value value) {
		List<Object> paramValues = new ArrayList<>();
    	if(isDataTypeSpecificFunction) {
    		paramValues.add(value.asObject());
    	}
    	for(ExprContext expr :ctx.expr()) {
    		Value paramValue = facilioWorkflowFunctionVisitor.visit(expr);
    		paramValues.add(paramValue.asObject());
    	}
    	return paramValues;
	}
	
	public static WorkflowNamespaceContext getNameSpace(String name) throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		WorkflowNamespaceContext workflowNamespaceContext = null;
		if (props != null && !props.isEmpty()) {
			workflowNamespaceContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowNamespaceContext.class);
		}
		return workflowNamespaceContext;
	}
	
	public static WorkflowContext getWorkflowFunction(String nameSpace,String functionName) throws Exception {
		
		WorkflowNamespaceContext workflowNamespaceContext = getNameSpace(nameSpace);
		return getWorkflowFunction(workflowNamespaceContext.getId(), functionName);
	}
	
	public static WorkflowContext getWorkflowFunction(Long nameSpaceId,String functionName) throws Exception {
		
		FacilioModule module = ModuleFactory.getWorkflowModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), functionName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), nameSpaceId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowContext.class);
		}
		return workflowContext;
	}
	
	
	
	
	
	
	
	
	
	
}
