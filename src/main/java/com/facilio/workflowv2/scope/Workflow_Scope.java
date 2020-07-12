package com.facilio.workflowv2.scope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowAssetCategoryReadingContext;
import com.facilio.workflowv2.contexts.WorkflowCategoryReadingContext;
import com.facilio.workflowv2.contexts.WorkflowModuleDataContext;
import com.facilio.workflowv2.contexts.WorkflowSpaceCategoryReadingContext;
import com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl;

public enum Workflow_Scope implements  ScopeInterface{

	DEFAULT(1,"module") {
		@Override
		public Object getObject(String name,WorkflowFunctionVisitor visitor,List<ExprContext> exprs) throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(name);
        	
        	if(module == null) {
        		return null;
        	}
        	
        	return getWorkflowModuleDataContext(module, visitor, exprs);
		}
		
		private WorkflowModuleDataContext getWorkflowModuleDataContext(FacilioModule module,WorkflowFunctionVisitor visitor,List<ExprContext> exprs) throws Exception {
			
			DBParamContext dbParam = null;
			
			List<Long> parentIds = new ArrayList<>();
			
			if(exprs.size() == 1) {
        		Value expr = visitor.visit(exprs.get(0));
        		if(expr.asObject() instanceof Criteria) {
        			Criteria criteria = expr.asCriteria();
        			dbParam = new DBParamContext();
        			dbParam.addAndCriteria(criteria);
        		}
        		else if(expr.asObject() instanceof Collection) {
        			List<Object> parentIdList = expr.asList();
        			
        			for(Object parentIdObj : parentIdList) {
        				parentIds.add((Long) parentIdObj);
        			}
        		}
        		else {
        			Long parentID = expr.asLong();
        			parentIds.add(parentID);
        		}
        	}
        	else {
        		for(ExprContext expr :exprs) {
        			Value exprValue = visitor.visit(expr);
        			parentIds.add(exprValue.asLong());
        		}
        	}
			
			return new WorkflowModuleDataContext(module, parentIds, dbParam);
		}
		
	},
	READING(2,"reading") {
		
		@Override
		public Object getObject(String moduleName,WorkflowFunctionVisitor visitor,List<ExprContext> exprs) throws Exception {
			
			List<Long> parentIDs = getParentIDs(visitor, exprs);
			
			WorkflowCategoryReadingContext wfCategoryReading = getWorkflowCategoryReading(moduleName, parentIDs);
			
			return wfCategoryReading;
		}
		
		private List<String> getSpaceType() {
			
			List<String> spaceType = new ArrayList<>();
			spaceType.add(FacilioConstants.ContextNames.SITE);
			spaceType.add(FacilioConstants.ContextNames.BUILDING);
			spaceType.add(FacilioConstants.ContextNames.FLOOR);
			spaceType.add(FacilioConstants.ContextNames.SPACE);
			
			return spaceType;
		}
		
		private WorkflowCategoryReadingContext getWorkflowCategoryReading(String name,List<Long> parentIDs) throws Exception {
			
			
			if(getSpaceType().contains(name)) {
				SpaceType spaceType = BaseSpaceContext.SpaceType.getModuleMap().get(name);
				if(spaceType == null) {
					return null;
				}
				
				WorkflowSpaceCategoryReadingContext spaceCategoryReadingContext = new WorkflowSpaceCategoryReadingContext(spaceType, parentIDs);
				
				return spaceCategoryReadingContext;
			}
			else {
				AssetCategoryContext category = AssetsAPI.getCategory(name);
				
				if(category == null) {
					return null;
				}
				
				WorkflowAssetCategoryReadingContext assetCategoryReadingContext = new WorkflowAssetCategoryReadingContext(category, parentIDs);
				
				return assetCategoryReadingContext;
			}
		}
		
		private List<Long> getParentIDs(WorkflowFunctionVisitor visitor,List<ExprContext> exprs) {
			List<Long> parentIDs = new ArrayList<>();
			for(ExprContext expr : exprs) {
				Value value = visitor.visit(expr);
				if(value.asObject() instanceof Collection) {
					List<Object> list = value.asList();
					for(Object no : list) {
						if(no instanceof Long) {
							parentIDs.add((Long)no);
				    	}
						else {
							Double d = Double.parseDouble(no.toString());
							parentIDs.add(d.longValue());
						}
					}
				}
				else {
					parentIDs.add(value.asLong());
				}
			}
			return parentIDs;
		}
	},
	;

	int order;
	String name;

	public int getIntVal() {
		return order;
	}
	
	public String getName() {
		return name;
	}

	private Workflow_Scope(int intVal,String name) {
		this.order = intVal;
		this.name = name;
	}

	private static final Map<Integer, Workflow_Scope> optionMap = Collections.unmodifiableMap(initTypeMap());

	private static Map<Integer, Workflow_Scope> initTypeMap() {
		Map<Integer, Workflow_Scope> typeMap = new HashMap<>();

		for (Workflow_Scope type : values()) {
			typeMap.put(type.getIntVal(), type);
		}
		return typeMap;
	}

	public static Map<Integer, Workflow_Scope> getAllOptions() {
		return optionMap;
	}
	
	private static final Map<String, Workflow_Scope> scopeNameMap = Collections.unmodifiableMap(initNameMap());

	private static Map<String, Workflow_Scope> initNameMap() {
		Map<String, Workflow_Scope> typeMap = new HashMap<>();

		for (Workflow_Scope type : values()) {
			typeMap.put(type.getName(), type);
		}
		return typeMap;
	}

	public static Map<String, Workflow_Scope> getNameMap() {
		return scopeNameMap;
	}

}
