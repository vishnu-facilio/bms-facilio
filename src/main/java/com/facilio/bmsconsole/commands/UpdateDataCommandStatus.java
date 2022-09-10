package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.iotmessage.IotData;
import com.facilio.agentv2.iotmessage.IotMessage;
import com.facilio.agentv2.iotmessage.IotMessageApiV2;
import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.PostTransactionCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlActionCommandContext.Status;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;

import lombok.extern.log4j.Log4j;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class UpdateDataCommandStatus extends AgentV2Command {
	
	IotData data;
	
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = agentBean.getAgent((String) payload.get("agent"));

            int maxRetryCount = agent.getCommandMaxRetryCount();
            if (payload.containsKey("controlIds")) {
            	
            	JSONArray controlIds = (JSONArray) payload.get("controlIds");
                JSONArray data = (JSONArray) payload.get("data");
                JSONObject jsonObject = (JSONObject) data.get(0);

                ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
                List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
                
                FacilioField statusField = fieldMap.get("status");
                FacilioField retriedCountField = fieldMap.get("retriedCount");

                List<ControlActionCommandContext> commands= getPointFieldsWithPointName(modbean,controlActionModule, fields, statusField, controlIds);
                List<ControlActionCommandContext> commandsToRetry = new ArrayList<ControlActionCommandContext>();

                List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();
                for (ControlActionCommandContext command : commands){
                    Long commandId = command.getId();
                    Integer retriedCount = command.getRetriedCount();
                    if (retriedCount == null) {
                    	retriedCount = 0;
                    }
                    String pointName = (String) command.getDatum("name");
                    String sentValue = command.getValue();
                    Object receivedValue = jsonObject.get(pointName);

                    GenericUpdateRecordBuilder.BatchUpdateByIdContext batchUpdate = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                    batchUpdate.setWhereId(commandId);
                    batchUpdateList.add(batchUpdate);
                    
                    Status status;
                    // On sending null to agents like niagara, the value received will be latest and not null 
                    if( receivedValue != null && (sentValue == null || sentValue.equals(receivedValue.toString()) )){
                    	status = Status.SUCCESS;
                    }
                    else if(receivedValue != null && maxRetryCount > 0 && maxRetryCount > retriedCount ){
                    	status = Status.RETRYING;
                        batchUpdate.addUpdateValue("retriedCount", ++retriedCount);
                        command.setRetriedCount(retriedCount);
                        commandsToRetry.add(command);
                    }
                    else {
                    	status = Status.FAILED;
                    }
                    batchUpdate.addUpdateValue("status", status.getIntVal());
                }


                List<FacilioField> updateFields = new ArrayList<>();
                updateFields.add(statusField);
                updateFields.add(retriedCountField);

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(controlActionModule.getTableName())
                        .fields(updateFields);

                if(batchUpdateList!= null &&!batchUpdateList.isEmpty()) {
                    int rows = updateRecordBuilder.batchUpdateById(batchUpdateList);
                    LOGGER.info("Number of rows updated: "+rows);
                }
                
                if (!commandsToRetry.isEmpty()) {
                	sendRetryCommands(commandsToRetry);
                }
                
            }
        } catch(Exception e) {
            LOGGER.error("Exception occurred on updating command status", e);
        }
        return false;
    }

    public List<ControlActionCommandContext> getPointFieldsWithPointName(ModuleBean modbean, FacilioModule controlActionModule, List<FacilioField> fields, FacilioField statusField, JSONArray controlIds) throws Exception {

        fields.add(FieldFactory.getField("name","Name",ModuleFactory.getPointModule(), FieldType.STRING));
        List<Long> statusList = Arrays.asList((long)ControlActionCommandContext.Status.SENT.getIntVal(), (long)ControlActionCommandContext.Status.RETRYING.getIntVal());
        
        SelectRecordsBuilder<ControlActionCommandContext> selectBuilder = new SelectRecordsBuilder<ControlActionCommandContext>()
                .select(fields)
                .module(controlActionModule)
                .beanClass(ControlActionCommandContext.class)
                .innerJoin(ModuleFactory.getPointModule().getTableName())
                .on(controlActionModule.getTableName()+".RESOURCE_ID="+ModuleFactory.getPointModule().getTableName()+".RESOURCE_ID AND "+controlActionModule.getTableName()+".FIELD_ID="+ModuleFactory.getPointModule().getTableName()+".FIELD_ID")
                .andCondition(CriteriaAPI.getIdCondition(controlIds, controlActionModule))
                .andCondition(CriteriaAPI.getCondition(statusField, statusList,NumberOperators.EQUALS));


        List<ControlActionCommandContext> props = selectBuilder.get();
        return props;

    }
    
    private void sendRetryCommands(List<ControlActionCommandContext> commandsToRetry) throws Exception {
    	FacilioChain chain = TransactionChainFactory.getExecuteControlActionCommandChain(false);
    	FacilioContext context = chain.getContext();
    	context.put(ControlActionUtil.CONTROL_ACTION_COMMANDS, commandsToRetry);
    	context.put(ControlActionUtil.CONTROL_ACTION_COMMAND_EXECUTED_FROM, ControlActionCommandContext.Control_Action_Execute_Mode.MANUAL);
    	chain.execute();
    }
    
}
