package com.facilio.workflowv2.scope;

import java.util.List;

import com.facilio.db.criteria.Criteria;
import com.facilio.scriptengine.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;

public interface ScopeInterface {
	public Object getObject(String moduleName,WorkflowFunctionVisitor visitor,List<ExprContext> exprs) throws Exception;
}
