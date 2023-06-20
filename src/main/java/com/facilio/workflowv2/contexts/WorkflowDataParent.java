package com.facilio.workflowv2.contexts;

import java.util.List;

import com.facilio.db.criteria.Criteria;
import com.facilio.scriptengine.context.DBParamContext;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;

public abstract class WorkflowDataParent {

	List<Long> parentIds;

	public List<Long> getParentIds() {
		return parentIds;
	}

	public void setParentIds(List<Long> parentIds) {
		this.parentIds = parentIds;
		if(getParentIds() != null && !getParentIds().isEmpty()) {
			if(getDbParam() == null) {
				setDbParam(new DBParamContext());
			}
			getDbParam().addAndCriteria(getParentIdCriteria());
		}
	}
	
	DBParamContext dbParam;

	public DBParamContext getDbParam() {
		return dbParam;
	}
	
	public void setDbParam(DBParamContext dbParam) {
		if(this.dbParam != null) {
			if(this.dbParam.getCriteria() != null) {
				dbParam.addAndCriteria(this.dbParam.getCriteria());
			}
		}
		this.dbParam = dbParam;
	}
	
	public boolean isSingleParent() {
		if(parentIds != null && parentIds.size() == 1) {
			return true;
		}
		return false;
	}
	
	public abstract Criteria getParentIdCriteria();
	public abstract Object fetchResult(WorkflowFunctionVisitor visitor) throws Exception;
	
}
