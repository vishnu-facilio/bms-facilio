package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import org.apache.commons.chain.Context;

import java.time.Instant;
import java.time.ZonedDateTime;

public class CreateActionForControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionTemplateId = (Long) context.get("controlActionTemplateId");
        Long scheduleActionTime = (Long) context.get("scheduledActionDateTime");
        Long revertActionTime = (Long) context.get("revertActionDateTime");
        Boolean isSandBox = (Boolean) context.get("isSandBox");

        if(controlActionTemplateId == null){
            throw new IllegalArgumentException("Control Action Template Id is Null");
        }
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);
        if(scheduleActionTime == null){
            throw new IllegalArgumentException("Please Give Schedule Action Date Time");
        }
        controlActionTemplateContext.setScheduledActionDateTime(scheduleActionTime);
        if(revertActionTime != null){
            controlActionTemplateContext.setRevertActionDateTime(revertActionTime);
        }
        if(isSandBox){
            controlActionTemplateContext.setControlActionExecutionType(V3ControlActionContext.ControlActionExecutionType.SANDBOX.getVal());
        }
        if(!isSandBox){
            controlActionTemplateContext.setControlActionExecutionType(V3ControlActionContext.ControlActionExecutionType.ACTUAL.getVal());
        }
        ControlActionAPI.updateControlActionTemplate(controlActionTemplateContext);
        ControlActionAPI.generateControlActionFromTemplateWms(controlActionTemplateId,null,null);

        return false;
    }
}
