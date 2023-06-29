package com.facilio.workflowv2.bean;

import com.facilio.chain.FacilioContext;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class ScriptBeanCacheImpl extends ScriptBeanImpl implements ScriptBean {

	private void invalidateNameSpaceCache(WorkflowNamespaceContext nameSpace) {
		if (nameSpace != null) {
			FacilioCache<String, WorkflowNamespaceContext> nameSpaceCache = LRUScriptCache.getScriptNameSpaceCache();
			String key = LRUScriptCache.CacheKeys.SCRIPT_NAMESPACE_KEY(getOrgId(), nameSpace.getName());
			nameSpaceCache.remove(key);
		}
	}

	@Override
	public WorkflowNamespaceContext addNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		// TODO Auto-generated method stub
		nameSpace = super.addNameSpace(nameSpace);
		invalidateNameSpaceCache(nameSpace);
		return nameSpace;
	}

	@Override
	public WorkflowNamespaceContext updateNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		// TODO Auto-generated method stub
		nameSpace = super.updateNameSpace(nameSpace);
		invalidateNameSpaceCache(nameSpace);
		return nameSpace;
	}

	@Override
	public WorkflowNamespaceContext deleteNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		// TODO Auto-generated method stub
		nameSpace = super.deleteNameSpace(nameSpace);
        invalidateNameSpaceCache(nameSpace);
		return nameSpace;
	}

	@Override
	public void deleteNameSpacesForIds(List<Long> ids) throws Exception {
		super.deleteNameSpacesForIds(ids);
		Map<Long, WorkflowNamespaceContext> nameSpacesForIds = UserFunctionAPI.getNameSpacesForIds(ids);
		if (MapUtils.isNotEmpty(nameSpacesForIds)) {
			for (WorkflowNamespaceContext nameSpace : nameSpacesForIds.values()) {
				invalidateNameSpaceCache(nameSpace);
			}
		}
	}

	@Override
	public WorkflowNamespaceContext getNameSpace(String nameSpaceName) throws Exception {
		// TODO Auto-generated method stub
		FacilioCache<String, WorkflowNamespaceContext> nameSpaceCache = LRUScriptCache.getScriptNameSpaceCache();
        String key = LRUScriptCache.CacheKeys.SCRIPT_NAMESPACE_KEY(getOrgId(), nameSpaceName);
        return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(nameSpaceCache, key, () ->super.getNameSpace(nameSpaceName));
	}
	
	
	@Override
	public FacilioContext addFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioContext context = super.addFunction(function);
		function = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
        if (function != null) {
             FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
            String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), function.getNameSpaceName(), function.getName());
            functionCache.remove(key);
        }
		
		return context;
		
	}

	@Override
	public FacilioContext updateFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext olduserFunction = UserFunctionAPI.getUserFunction(function.getId());
		FacilioContext context = super.updateFunction(function);
		
		function = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
        if (olduserFunction != null) {
             FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
            String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), olduserFunction.getNameSpaceName(), olduserFunction.getName());
            functionCache.remove(key);
        }
		
		return context;
	}

	@Override
	public WorkflowUserFunctionContext deleteFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userfunction = UserFunctionAPI.getUserFunction(function.getId());
		
		function = super.deleteFunction(function);
		
		if (userfunction != null) {
            FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
           String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), userfunction.getNameSpaceName(), userfunction.getName());
           functionCache.remove(key);
        }
		
		return function;
	}

	@Override
	public void deleteFunctionsForIds(List<Long> ids) throws Exception {
		super.deleteFunctionsForIds(ids);
		Map<Long, WorkflowUserFunctionContext> functionsForIds = UserFunctionAPI.getFunctionsForIds(ids, false);
		if (MapUtils.isNotEmpty(functionsForIds)) {
			for (WorkflowUserFunctionContext functionContext : functionsForIds.values()) {
				FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
				String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), functionContext.getNameSpaceName(), functionContext.getName());
				functionCache.remove(key);
			}
		}
	}

	@Override
	public WorkflowContext deleteFunction(WorkflowContext function) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userfunction = UserFunctionAPI.getUserFunction(function.getId());
		function = super.deleteFunction(function);
		
        if (userfunction != null) {
             FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
            String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), userfunction.getNameSpaceName(), userfunction.getName());
            functionCache.remove(key);
        }
		
		return function;
	}

	@Override
	public WorkflowUserFunctionContext getFunction(String nameSpaceName, String functionName) throws Exception {
		// TODO Auto-generated method stub
		FacilioCache<String, WorkflowUserFunctionContext> functionCache = LRUScriptCache.getScriptFunctionCache();
        String key = LRUScriptCache.CacheKeys.SCRIPT_FUNCTION_KEY(getOrgId(), nameSpaceName, functionName);
        WorkflowUserFunctionContext workflowContext = FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(functionCache, key, () ->super.getFunction(nameSpaceName,functionName));
        
        workflowContext.setReturnValue(null);
        workflowContext.setParams(null);
        workflowContext.setParameters(null);
        
        return workflowContext;
	}
}
