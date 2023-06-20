package com.facilio.workflowv2.contexts;

import java.util.ArrayList;
import java.util.List;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl;

public abstract class WorkflowCategoryReadingContext extends WorkflowDataParent {
	
	@Override
	public Criteria getParentIdCriteria() {
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getConditionFromList(null, "parentId", getParentIds(), NumberOperators.EQUALS));
		return criteria;
		
	}

	public abstract FacilioField getField(String fieldName) throws Exception;
	
	@Override
	public Object fetchResult(WorkflowFunctionVisitor visitor) throws Exception {
		
		ScriptContext scriptContext = visitor.getScriptContext();
		
		List<Object> params = new ArrayList<>();
		
		FacilioField field = this.getField(getDbParam().getFieldName());
		
    	FacilioModule module = field.getModule();
		
		params.add(module);
		params.add(getDbParam());
		
		FacilioModuleFunctionImpl functions = new FacilioModuleFunctionImpl();
		Object result = functions.fetch(visitor.getGlobalParam(), params,scriptContext);
		
		if(isSingleParent() && result instanceof List) {
			List<Object> resultList = (List<Object>)result;
			
			result = resultList.get(0);
		}
		
		return result;
	}
}
