package com.facilio.workflowv2.bean;

import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.PubSubLRUCache;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class LRUScriptCache {

	private static FacilioCache<String, WorkflowUserFunctionContext> scriptFunctionCache = new PubSubLRUCache<>("scriptFunction",5000);
	private static FacilioCache<String, WorkflowNamespaceContext> scriptNameSpaceCache = new PubSubLRUCache<>("scriptNameSapce",2000);
	
	public static class CacheKeys {

        private static final String SCRIPT_NAMESPACE_KEY = "scriptNameSpaceCache";
        private static final String SCRIPT_FUNCTION_KEY = "scriptFunctionCache";
        
        public static final String SCRIPT_NAMESPACE_KEY(@NonNull Long orgId, @NonNull String nameSpace) {
            return new StringBuilder()
                    .append(SCRIPT_NAMESPACE_KEY)
                    .append(CacheUtil.KEY_SEPARATOR)
                    .append(orgId)
                    .append(CacheUtil.KEY_SEPARATOR)
                    .append(nameSpace)
                    .toString();
        }

        
        public static final String SCRIPT_FUNCTION_KEY (@NonNull Long orgId, @NonNull String nameSpace,@NonNull String functionName) {
            return new StringBuilder()
                    .append(SCRIPT_FUNCTION_KEY)
                    .append(CacheUtil.KEY_SEPARATOR)
                    .append(orgId)
                    .append(CacheUtil.KEY_SEPARATOR)
                    .append(nameSpace)
                    .append(CacheUtil.KEY_SEPARATOR)
                    .append(functionName)
                    .toString();
        }
    }

	public static FacilioCache<String, WorkflowNamespaceContext> getScriptNameSpaceCache() {
		return scriptNameSpaceCache;
	}

	public static FacilioCache<String, WorkflowUserFunctionContext> getScriptFunctionCache() {
		return scriptFunctionCache;
	}
}
