package com.facilio.workflowv2.bean;

import com.facilio.client.app.pojo.ClientAppInfo;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.PubSubLRUCache;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class FWLRUScriptCache {

	private static FacilioCache<String, WorkflowUserFunctionContext> scriptFunctionCache = new PubSubLRUCache<>("scriptFunction",5000);
	private static FacilioCache<String, WorkflowNamespaceContext> ScriptNameSpaceCache = new PubSubLRUCache<>("scriptNameSapce",2000);
	
	public static class CacheKeys {
        private static final String KEY_SEPARATOR = "#";

        private static final String SCRIPT_NAMESPACE_KEY = "scriptNameSpaceCache";
        private static final String SCRIPT_FUNCTION_KEY = "scriptFunctionCache";
        
        public static final String SCRIPT_NAMESPACE_KEY(@NonNull Long orgId, @NonNull String nameSpace) {
            return new StringBuilder()
                    .append(SCRIPT_NAMESPACE_KEY)
                    .append(KEY_SEPARATOR)
                    .append(orgId)
                    .append(KEY_SEPARATOR)
                    .append(nameSpace)
                    .toString();
        }

        
        public static final String SCRIPT_FUNCTION_KEY (@NonNull Long orgId, @NonNull String nameSpace,@NonNull String functionName) {
            return new StringBuilder()
                    .append(SCRIPT_FUNCTION_KEY)
                    .append(KEY_SEPARATOR)
                    .append(orgId)
                    .append(KEY_SEPARATOR)
                    .append(nameSpace)
                    .append(KEY_SEPARATOR)
                    .append(functionName)
                    .toString();
        }
    }

	public static FacilioCache<String, WorkflowNamespaceContext> getScriptNameSpaceCache() {
		return ScriptNameSpaceCache;
	}

	public static FacilioCache<String, WorkflowUserFunctionContext> getScriptFunctionCache() {
		return scriptFunctionCache;
	}
}
