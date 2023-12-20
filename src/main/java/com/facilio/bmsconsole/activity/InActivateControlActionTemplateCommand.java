package com.facilio.bmsconsole.activity;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class InActivateControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionTemplateId = (Long) context.get("controlActionTemplateId");
        Boolean ignoreActivityMsg = (Boolean) context.getOrDefault("ignoreActivityMsg",false);
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);
        controlActionTemplateContext.setControlActionTemplateStatus(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getVal());
        controlActionTemplateContext.setScheduledActionDateTime(-99L);
        controlActionTemplateContext.setRevertActionDateTime(-99L);
        controlActionTemplateContext.setControlActionExecutionType(V3ControlActionContext.ControlActionExecutionType.ACTUAL.getVal());
        ControlActionAPI.updateControlActionTemplate(controlActionTemplateContext);
        if(ignoreActivityMsg) {
            ControlActionAPI.addControlActionTemplateActivity(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getValue(), controlActionTemplateId);
        }
        if(controlActionTemplateContext.getControlActionTemplateType() == V3ControlActionTemplateContext.ControlActionTemplateType.SCHEDULED.getVal()) {
            ControlActionAPI.dropControlActionsOfControlActionTemplate(controlActionTemplateId);
        }
        return false;
    }
}
