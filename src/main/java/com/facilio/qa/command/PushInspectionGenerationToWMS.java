package com.facilio.qa.command;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.InspectionGenerationHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class PushInspectionGenerationToWMS extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("After Transaction Command for Inspection generation");
        List<InspectionTriggerContext> triggers = (List<InspectionTriggerContext>) context.get(FacilioConstants.Inspection.INSPECTION_TRIGGERS);
        List<BaseScheduleContext> baseSchedules = triggers.stream().map(InspectionTriggerContext::getSchedule).collect(Collectors.toList());
        if(baseSchedules!=null && !baseSchedules.isEmpty()) {
            LOGGER.info("Base Schedules Size : "+baseSchedules.size());
            for(BaseScheduleContext baseSchedule:baseSchedules) {
                JSONObject baseScheduleListObject = new JSONObject();
                baseScheduleListObject.put(com.facilio.qa.rules.Constants.Command.BASESCHEDULES, baseSchedule);
                Messenger.getMessenger().sendMessage(new Message()
                        .setKey(InspectionGenerationHandler.KEY+"/"+baseSchedule.getId())
                        .setContent(baseScheduleListObject));
            }
            LOGGER.info("Successfully Pushed Inspection generation to kafka");
        }
        return false;
    }
}
