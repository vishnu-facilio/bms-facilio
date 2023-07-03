package com.facilio.blockfactory.blocks;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.enums.FlowVariableDataType;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScriptBlock extends BaseBlock{

    private long functionId;
    private String variableName;
    private FlowVariableDataType dataType;
    private List<ScriptFlowParameter> parameters;
    private JSONArray parametersJSONArray;
    public ScriptBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            init();
            FacilioChain functionChain = TransactionChainFactory.getExecuteWorkflowChain();
            FacilioContext functionContext = functionChain.getContext();

            WorkflowUserFunctionContext userFunction = UserFunctionAPI.getUserFunction(functionId);
            if(userFunction == null){
                throw new FlowException("functionId:"+functionId+ " does not exist in ScriptBlock");
            }
            userFunction.fillFunctionHeaderFromScript();
            userFunction.setLogNeeded(true);
            functionContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, userFunction);

            JSONObject currentRecord = super.getFlowEngineInterFace().getCurrentRecord();

            List<ParameterContext> functionParameters = userFunction.getParameters();

            if(CollectionUtils.isNotEmpty(functionParameters)){
                Map<String,ParameterContext> functionParamMap = functionParameters.stream().collect(Collectors.toMap(ParameterContext::getName, Function.identity()));
                Map<String,ScriptFlowParameter> scriptFlowParameterMap = parameters.stream().collect(Collectors.toMap(ScriptFlowParameter::getParameterName,Function.identity()));
                List<Object> paramsList = new ArrayList<>();
                for (String functionParamName:functionParamMap.keySet()){
                    ScriptFlowParameter scriptFlowParam = scriptFlowParameterMap.get(functionParamName);
                    Object paramValue = FlowEngineUtil.replacePlaceHolder(scriptFlowParam.getParameterValue(),memory);
                    paramsList.add(paramValue);
                }
                functionContext.put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
            }

            if(currentRecord != null && currentRecord.get("id")!=null) {
                functionContext.put(FacilioConstants.Workflow.WORKFLOW_LOG_RECORD_ID, currentRecord.get("id"));
            }

            functionContext.put(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_ID, id);
            functionContext.put(FacilioConstants.Workflow.WORKFLOW_LOG_PARENT_TYPE, WorkflowLogContext.WorkflowLogType.FLOW);

            functionChain.execute(functionContext);

            if(userFunction.getReturnTypeEnum()!=null && StringUtils.isNotEmpty(variableName) && dataType!=null){
                Object returnValue = userFunction.getReturnValue();
                if(!dataType.checkDataType(returnValue)){
                    throw new FlowException("Data type mismatch:"+variableName+"'s data type is "+dataType.getDataType()+" but script function return data type is "+userFunction.getReturnTypeEnum().getStringValue());
                }
                memory.put(variableName,returnValue);
            }

        }catch (Exception exception) {
            if(exception instanceof FlowException){
                throw (FlowException) exception;
            }else {
                throw new FlowException(exception.getMessage());
            }
        }
    }
    private void init() throws IOException {
        functionId = (long) config.get(Constants.FUNCTION_ID);
        variableName = (String) config.get(Constants.VARIABLE_NAME);
        String dataTypeStr = (String)config.get(Constants.DATA_TYPE);
        if(StringUtils.isNotEmpty(dataTypeStr)){
            this.dataType = FlowVariableDataType.getDataType(dataTypeStr);
        }
        parametersJSONArray = (JSONArray) config.get(Constants.PARAMETERS);
        parameters = FieldUtil.getAsBeanListFromJsonArray(parametersJSONArray,ScriptFlowParameter.class);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object functionId = config.get(Constants.FUNCTION_ID);
        Object variableName = config.get(Constants.VARIABLE_NAME);
        Object dataTypeStr = config.get(Constants.DATA_TYPE);
        Object parametersJSONArray = config.get(Constants.PARAMETERS);
        if(functionId == null){
            throw new FlowException("functionId is can not be null");
        }
        if(!(functionId instanceof Long)){
            throw new FlowException("functionId is not a number");
        }

        if(variableName!=null && !(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for ScriptBlock");
        }
        if(dataTypeStr!=null){
            if(!(dataTypeStr instanceof String)){
                throw new FlowException("dataType:'"+variableName+"' is not a string for ScriptBlock");
            }
            if(FlowVariableDataType.getDataType(dataTypeStr.toString())==null){
                throw new FlowException("Unsupported data type "+dataType);
            }
        }
        if(parametersJSONArray!=null){
            if(!(parametersJSONArray instanceof JSONArray)){
                throw new FlowException("parameters:"+parametersJSONArray+" is not a JSONArray for ScriptBlock");
            }
            try{
                 FieldUtil.getAsBeanListFromJsonArray((JSONArray) parametersJSONArray,ScriptFlowParameter.class);
            }catch (Exception e){
                throw new FlowException("Failed to convert parameters to ScriptFlowParameter instance:"+e.getMessage());
            }
        }
    }
    @Getter@Setter
    public static class ScriptFlowParameter {
        public ScriptFlowParameter(){
        }
        private String parameterName;
        private String parameterValue;
    }
}
