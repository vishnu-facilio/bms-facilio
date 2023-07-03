package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;

public class SummaryRecordBlock extends BaseBlock {
    private String variableName;
    private Object recordId;
    private String moduleName;
    public SummaryRecordBlock(Map<String,Object> config){
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
                throw new FlowException("recordId cannot be empty for SummaryRecordBlock");
            }
            if(!NumberUtils.isNumber(recordIdOb.toString())){
                throw new FlowException("recordId is not a number for SummaryRecordBlock");
            }

            String moduleName = moduleNameOb.toString();
            long recId = (long)Double.parseDouble(recordIdOb.toString());

            ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(module == null){
                throw new FlowException("SummaryRecordBlock: "+moduleName+" Module is not found");
            }

            Object record = V3Util.getRecord(moduleName,recId,null);
            memory.put(variableName, FieldUtil.getAsProperties(record));
        }catch (Exception exception){
            if(exception instanceof FlowException){
                throw (FlowException) exception;
            }else {
                throw new FlowException(exception.getMessage());
            }
        }
    }
    private void init(){
        this.variableName= (String) config.get(Constants.VARIABLE_NAME);
        this.moduleName = (String) config.get(Constants.MODULE_NAME);
        this.recordId = config.get(Constants.RECORD_ID);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object variableName = config.get(Constants.VARIABLE_NAME);
        Object moduleName = config.get(Constants.MODULE_NAME);
        Object recordId = config.get(Constants.RECORD_ID);
        if(variableName == null){
            throw new FlowException("variableName can not be empty for SummaryRecordBlock");
        }
        if(!(variableName instanceof String)){
            throw new FlowException("variableName:'"+variableName+"' not a string for SummaryRecordBlock");
        }
        if(recordId == null){
            throw new FlowException("recordId cannot be empty for SummaryRecordBlock");
        }
        if(moduleName == null){
            throw new FlowException("moduleName cannot be empty for SummaryRecordBlock");
        }
        if(!(moduleName instanceof String)){
            throw new FlowException("moduleName:'"+moduleName+"' not a string for SummaryRecordBlock");
        }
    }
}
