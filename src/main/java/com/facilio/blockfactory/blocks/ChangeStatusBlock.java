package com.facilio.blockfactory.blocks;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.flowengine.exception.FlowException;
import com.facilio.flowengine.executor.FlowEngineUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.util.V3Util;

import java.util.Map;

public class ChangeStatusBlock extends BaseBlock{
    private Object recordId;
    private String moduleName;
    private String newState;
    public ChangeStatusBlock(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void execute(Map<String, Object> memory) throws FlowException {
        try{
            init();

            long recId = -1l;
            String moduleName = null;
            String statusName = null;

            Object ob = FlowEngineUtil.evaluateExpression(memory,recordId.toString());
            Object moduleNameOb = FlowEngineUtil.evaluateExpression(memory, this.moduleName);
            Object statusNameOb = FlowEngineUtil.evaluateExpression(memory,this.newState);
            if(!(ob instanceof Number)){
                throw new FlowException("recordId is not a number for ChangeStatusBlock");
            }
            if(!(moduleNameOb instanceof  String)){
                throw new FlowException("moduleName is not a string");
            }
            if(!(statusNameOb instanceof  String)){
                throw new FlowException("statusName is not a string");
            }

            recId = (long) Double.parseDouble(ob.toString());
            moduleName = moduleNameOb.toString();
            statusName = statusNameOb.toString();

            ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);

            if(module == null){
                throw new FlowException("ChangeStatusBlock: "+moduleName+" Module is not found");
            }

            ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) V3Util.getRecord(moduleName,recId,null);

            if(record == null){
                throw new FlowException("Record not found to change status for recordId:"+recId);
            }

            FacilioStatus status = TicketAPI.getStatus(module, statusName);

            StateFlowRulesAPI.updateState(record, module, status, false, new FacilioContext());

        }catch (Exception e){
            if(e instanceof FlowException){
                throw (FlowException) e;
            }
            else {
                throw new FlowException(e.getMessage());
            }
        }

    }
    private void init(){
        this.newState= (String) config.get(com.facilio.flowengine.context.Constants.NEW_STATE);
        this.moduleName = (String) config.get(com.facilio.flowengine.context.Constants.MODULE_NAME);
        this.recordId = config.get(com.facilio.flowengine.context.Constants.RECORD_ID);
    }

    @Override
    public void validateBlockConfiguration() throws FlowException {
        Object newState = config.get(com.facilio.flowengine.context.Constants.NEW_STATE);
        Object moduleName = config.get(com.facilio.flowengine.context.Constants.MODULE_NAME);
        Object recordId = config.get(com.facilio.flowengine.context.Constants.RECORD_ID);
        if(newState == null){
            throw new FlowException("newState can not be empty for ChangeStatusBlock");
        }
        if(!(newState instanceof String)){
            throw new FlowException("newState:'"+newState+"' not a string for ChangeStatusBlock");
        }
        if(recordId == null){
            throw new FlowException("recordId cannot be empty for ChangeStatusBlock");
        }
        if(moduleName == null){
            throw new FlowException("moduleName cannot be empty for ChangeStatusBlock");
        }
        if(!(moduleName instanceof String)){
            throw new FlowException("moduleName:'"+moduleName+"' not a string for ChangeStatusBlock");
        }
    }
}
