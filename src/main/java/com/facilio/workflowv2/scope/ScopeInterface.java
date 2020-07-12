package com.facilio.workflowv2.scope;

import java.util.List;

import com.facilio.db.criteria.Criteria;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;

public interface ScopeInterface {
	public Object getObject(String moduleName,WorkflowFunctionVisitor visitor,List<ExprContext> exprs) throws Exception;
}
