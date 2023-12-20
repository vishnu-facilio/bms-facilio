package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import org.apache.commons.chain.Context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ActivateControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionTemplateId = (Long) context.get("controlActionTemplateId");
        Boolean ignoreActivityMsg = (Boolean) context.getOrDefault("ignoreActivityMsg",false);
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);
        controlActionTemplateContext.setControlActionTemplateStatus(V3ControlActionTemplateContext.ControlActionTemplateStatus.ACTIVE.getVal());
        controlActionTemplateContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal());
        ControlActionAPI.updateControlActionTemplate(controlActionTemplateContext);
        if(ignoreActivityMsg) {
            ControlActionAPI.addControlActionTemplateActivity(V3ControlActionTemplateContext.ControlActionTemplateStatus.ACTIVE.getValue(), controlActionTemplateId);
        }
        if(controlActionTemplateContext.getControlActionTemplateType() != V3ControlActionTemplateContext.ControlActionTemplateType.FLAGGED_EVENT.getVal()){
            Long currentTime = System.currentTimeMillis();
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(currentTime), DBConf.getInstance().getCurrentZoneId());
            ZonedDateTime startTime = zonedDateTime.plusDays(1);
            zonedDateTime = zonedDateTime.plusDays(5);
            ControlActionAPI.generateControlActionFromTemplateWms(controlActionTemplateId,startTime.toEpochSecond()*1000,zonedDateTime.toEpochSecond()*1000);
        }
        return false;
    }
}
