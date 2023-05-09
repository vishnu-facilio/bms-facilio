package com.facilio.qa.command;

import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.InspectionGenerationHandler;
import com.facilio.wmsv2.message.Message;
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
            JSONObject baseScheduleListObject = new JSONObject();
            baseScheduleListObject.put(com.facilio.qa.rules.Constants.Command.BASESCHEDULES, baseSchedules);
            SessionManager.getInstance().sendMessage(new Message()
                    .setTopic(InspectionGenerationHandler.TOPIC)
                    .setContent(baseScheduleListObject));
            LOGGER.info("Successfully Pushed Inspection generation to kafka");
        }
        return false;
    }
}
