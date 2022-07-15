package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

import lombok.extern.log4j.Log4j;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

@Log4j
public class UpdateDataCommandStatus extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            JSONObject payload = (JSONObject) context.get(AgentConstants.DATA);
            if (payload.containsKey("controlId")) {
                long controlId= (long) payload.get("controlId");
                JSONArray data = (JSONArray) payload.get("data");
                JSONObject jsonObject = (JSONObject) data.get(0);
                // Assuming only 1 point will be received for control cov.
                Object valueReceived = jsonObject.values().iterator().next();

                ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule controlActionModule = modbean.getModule(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
                List<FacilioField> fields = modbean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
                SelectRecordsBuilder<ControlActionCommandContext> builder = new SelectRecordsBuilder<ControlActionCommandContext>()
                        .module(controlActionModule)
                        .select(fields)
                        .beanClass(ControlActionCommandContext.class)
                        .andCondition(CriteriaAPI.getIdCondition(controlId,controlActionModule));
                ControlActionCommandContext command = builder.fetchFirst();
                String valueSent = command.getValue();

                GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                        .table(controlActionModule.getTableName())
                        .fields(fields)
                        .andCondition(CriteriaAPI.getIdCondition(controlId,controlActionModule));
                Map<String, Object> toUpdate = new HashMap<>();
                if (String.valueOf(valueReceived).equals(valueSent)) {
                    toUpdate.put("status", ControlActionCommandContext.Status.SUCCESS.getIntVal());
                } else {
                    toUpdate.put("status", ControlActionCommandContext.Status.FAILED.getIntVal());
                }
                updateRecordBuilder.update(toUpdate);
            }
        } catch(Exception e) {
            LOGGER.error("Exception occurred on updating command status", e);
        }
        return false;
    }
}
