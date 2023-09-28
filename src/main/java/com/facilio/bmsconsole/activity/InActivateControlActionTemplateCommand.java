package com.facilio.bmsconsole.activity;

import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionTemplateContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class InActivateControlActionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionTemplateId = (Long) context.get("controlActionTemplateId");
        V3ControlActionTemplateContext controlActionTemplateContext = ControlActionAPI.getControlActionTemplate(controlActionTemplateId);
        controlActionTemplateContext.setControlActionTemplateStatus(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getVal());
        ControlActionAPI.updateControlActionTemplate(controlActionTemplateContext);
        ControlActionAPI.addControlActionTemplateActivity(V3ControlActionTemplateContext.ControlActionTemplateStatus.IN_ACTIVE.getValue(),controlActionTemplateId);
        ControlActionAPI.dropControlActionsOfControlActionTemplate(controlActionTemplateId);
        return false;
    }
}
