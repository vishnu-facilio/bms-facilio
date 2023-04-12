package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.NotesAPI;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.systemfunctions.FacilioWorkflowFunctionInterface;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FacilioCommentsFunction implements FacilioWorkflowFunctionInterface {

    ADD_OR_UPDATE_COMMENT_SHARING(1,"addOrUpdateCommentsSharing") {
        @Override
        public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception {

            checkParam(objects);
            NotesAPI.addCommentSharing((String) objects[0],(Long) objects[1],(List<String>) objects[2]);

            return null;
        };

        public void checkParam(Object... objects) throws Exception {
            if(objects.length <= 0) {
                throw new FunctionParamException("Required Object is null");
            }
        }
    }
    ;

    private Integer value;
    private String functionName;
    private String namespace = "comments";
    private String params;
    private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.COMMENTS;

    public Integer getValue() {
        return value;
    }
    public void setValue(Integer value) {
        this.value = value;
    }
    public String getFunctionName() {
        return functionName;
    }
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
    public String getNamespace() {
        return namespace;
    }
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    public String getParams() {
        return params;
    }
    FacilioCommentsFunction(Integer value,String functionName) {
        this.value = value;
        this.functionName = functionName;
    }

    public static Map<String, FacilioCommentsFunction> getAllFunctions() {
        return DEFAULT_FUNCTIONS;
    }
    public static FacilioCommentsFunction getFacilioCommentsFunctions(String functionName) {
        return DEFAULT_FUNCTIONS.get(functionName);
    }
    static final Map<String, FacilioCommentsFunction> DEFAULT_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
    static Map<String, FacilioCommentsFunction> initTypeMap() {
        Map<String, FacilioCommentsFunction> typeMap = new HashMap<>();
        for(FacilioCommentsFunction type : FacilioCommentsFunction.values()) {
            typeMap.put(type.getFunctionName(), type);
        }
        return typeMap;
    }

}
