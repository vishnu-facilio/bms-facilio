package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.ConfigParams;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class CreateRecordBlock extends CRUDBaseBlock {
    private String variableName;
    private JSONObject recordData;
    private String moduleName;
    public CreateRecordBlock(Map<String,Object> config){
        super(config);
    }
    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            init();

            Object moduleNameOb = FlowEngineUtil.replacePlaceHolder(this.moduleName,memory);

            if(!(moduleNameOb instanceof  String)){
                throw new FlowException("CreateRecordBlock:moduleName is not a string");
            }

            String moduleName = moduleNameOb.toString();

            ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(module == null){
                throw new FlowException("CreateRecordBlock: "+moduleName+" Module is not found");
            }

            JSONObject replacedRecordData = (JSONObject) FlowEngineUtil.replacePlaceHolder(recordData,memory);

            validateRecordData(replacedRecordData,moduleName);

            ConfigParams configParams = new ConfigParams();
            configParams.setOnlyRestrictedWorkflows(true);

            FacilioContext context = V3Util.createRecord(module,replacedRecordData,false,null,null,true,configParams);
            List<ModuleBaseWithCustomFields> records = com.facilio.v3.context.Constants.getRecordList(context);
            if(CollectionUtils.isEmpty(records)){
                throw new FlowException("CreateRecordBlock:Record not created");
            }

            if(StringUtils.isNotEmpty(variableName)){
                memory.put(variableName, FieldUtil.getAsJSON(records.get(0)));
            }

        }catch (Exception exception){
            flowEngineInterFace.log(exception.getMessage());
            FlowException flowException = exception instanceof FlowException?(FlowException)exception:new FlowException(exception.getMessage());
            flowEngineInterFace.emitBlockError(this,memory,flowException);
            throw flowException;
        }
    }
    private void init(){
        this.variableName= (String) config.get(Constants.VARIABLE_NAME);
        this.moduleName = (String) config.get(Constants.MODULE_NAME);
        this.recordData = (JSONObject) config.get(Constants.RECORD_DATA);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object variableName = config.get(Constants.VARIABLE_NAME);
        Object moduleName = config.get(Constants.MODULE_NAME);
        Object recordData = config.get(Constants.RECORD_DATA);
        if(variableName!=null && !(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for CreateRecordBlock");
        }
        if(recordData == null){
            throw new FlowException("recordData cannot be empty for CreateRecordBlock");
        }
        if(!(recordData instanceof JSONObject)){
            throw new FlowException("recordData is not a JSONObject for CreateRecordBlock");
        }
        if(moduleName == null){
            throw new FlowException("moduleName cannot be empty for CreateRecordBlock");
        }
        if(!(moduleName instanceof String)){
            throw new FlowException("moduleName:'"+moduleName+"' not a string for CreateRecordBlock");
        }
    }
}
