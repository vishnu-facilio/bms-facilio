package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.ConfigParams;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;

import java.util.Map;

public class UpdateRecordBlock extends CRUDBaseBlock{
    private String variableName;
    private Object recordId;
    private JSONObject recordData;
    private String moduleName;
    public UpdateRecordBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            init();

            Object recordIdOb = FlowEngineUtil.replacePlaceHolder(recordId,memory);
            Object moduleNameOb = FlowEngineUtil.replacePlaceHolder(this.moduleName,memory);

            if(!(moduleNameOb instanceof  String)){
                throw new FlowException("moduleName is not a string");
            }
            if(recordIdOb == null){
                throw new FlowException("recordId cannot be empty for UpdateRecordBlock");
            }
            if(!NumberUtils.isNumber(recordIdOb.toString())){
                throw new FlowException("recordId is not a number for UpdateRecordBlock");
            }

            String moduleName = moduleNameOb.toString();
            long recId = (long)Double.parseDouble(recordIdOb.toString());

            ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(module == null){
                throw new FlowException("UpdateRecordBlock: "+moduleName+" Module is not found");
            }

            JSONObject replacedRecordData = (JSONObject) FlowEngineUtil.replacePlaceHolder(recordData,memory);

            validateRecordData(replacedRecordData,moduleName);

            ConfigParams configParams = new ConfigParams();
            configParams.setOnlyRestrictedWorkflows(true);

            JSONObject updatedRecordMap = V3Util.processAndUpdateSingleRecord(moduleName,recId,replacedRecordData,null,null,null,null,null,null,null,null,true,configParams);

            if(StringUtils.isNotEmpty(variableName)){
                Object updatedRecordOb = updatedRecordMap.get(moduleName);
                Map updatedRecord;
                if(updatedRecordOb instanceof Map){
                    updatedRecord = (Map) updatedRecordOb;
                }else{
                    updatedRecord = FieldUtil.getAsJSON(updatedRecordOb);
                }
                memory.put(variableName,updatedRecord);
            }
        }catch (Exception exception){
            if(exception instanceof FlowException){
                throw (FlowException) exception;
            }else {
                throw new FlowException(exception.getMessage());
            }
        }
    }
    private void init(){
        this.recordId = config.get(Constants.RECORD_ID);
        this.moduleName = (String) config.get(Constants.MODULE_NAME);
        this.recordData = (JSONObject) config.get(Constants.RECORD_DATA);
        this.variableName = (String) config.get(Constants.VARIABLE_NAME);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object recordId = config.get(Constants.RECORD_ID);
        Object moduleName = config.get(Constants.MODULE_NAME);
        Object recordData = config.get(Constants.RECORD_DATA);
        Object variableName = config.get(Constants.VARIABLE_NAME);

        if(variableName!=null && !(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for ScriptBlock");
        }

        if(recordId == null){
            throw new FlowException("recordId can not be empty for UpdateRecordBlock");
        }
        if(!(recordId instanceof String || recordId instanceof Number)){
            throw new FlowException("recordID should be either a number or placeholder");
        }
        if(recordData == null){
            throw new FlowException("recordData cannot be empty for UpdateRecordBlock");
        }
        if(!(recordData instanceof JSONObject)){
            throw new FlowException("recordData is not a JSONObject for UpdateRecordBlock");
        }
        if(moduleName == null){
            throw new FlowException("moduleName cannot be empty for UpdateRecordBlock");
        }
        if(!(moduleName instanceof String)){
            throw new FlowException("moduleName:'"+moduleName+"' not a string for UpdateRecordBlock");
        }
    }
}
