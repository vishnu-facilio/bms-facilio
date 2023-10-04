package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.bmsconsole.activity.ControlActionActivityType;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.Collections;

public class UnPublishControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionId = (Long) context.get("controlActionId");
        V3ControlActionContext controlActionContext = ControlActionAPI.getControlAction(controlActionId);
        controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getVal());
        ControlActionAPI.updateControlAction(controlActionContext);
        ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.UNPUBLISHED.getValue(),controlActionId);
        ControlActionAPI.dropControlActionCommands(controlActionId);
        return false;

    }
}
