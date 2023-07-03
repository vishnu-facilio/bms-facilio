package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.flowengine.context.Constants;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.util.V3Util;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeleteRecordBlock extends BaseBlock {
    private Object recordId;
    private String moduleName;

    public DeleteRecordBlock(Map<String, Object> config) throws Exception {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try {
            init();

            Object recordIdOb = FlowEngineUtil.replacePlaceHolder(recordId,memory);
            Object moduleNameOb = FlowEngineUtil.replacePlaceHolder(this.moduleName,memory);

            if(!(moduleNameOb instanceof  String)){
                throw new FlowException("moduleName is not a string");
            }
            if(recordIdOb == null){
                throw new FlowException("recordId cannot be empty for DeleteRecordBlock");
            }
            if(!NumberUtils.isNumber(recordIdOb.toString())){
                throw new FlowException("recordId is not a number for DeleteRecordBlock");
            }

            String moduleName = moduleNameOb.toString();
            long recId = (long)Double.parseDouble(recordIdOb.toString());

            ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(module == null){
                throw new FlowException("DeleteRecordBlock: "+moduleName+" Module is not found");
            }

            Map<String, Object> deleteObj = new HashMap<>();
            deleteObj.put(moduleName, Arrays.asList(recId));
            V3Util.deleteRecords(moduleName, deleteObj, null, null, true);
        } catch (Exception ex) {
            if (ex instanceof FlowException) {
                throw (FlowException)ex;
            } else {
                throw new FlowException(ex.getMessage());
            }
        }
    }
    private void init(){
        this.moduleName = (String) config.get(Constants.MODULE_NAME);
        this.recordId = config.get(Constants.RECORD_ID);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object moduleName = config.get(Constants.MODULE_NAME);
        Object recordId = config.get(Constants.RECORD_ID);
        if (recordId == null) {
            throw new FlowException("recordId cannot be empty for DeleteRecordBlock");
        }
        if (moduleName == null) {
            throw new FlowException("moduleName cannot be empty for DeleteRecordBlock");
        }
        if(!(moduleName instanceof String)){
            throw new FlowException("moduleName:'"+moduleName+"' not a string for DeleteRecordBlock");
        }
    }
}
