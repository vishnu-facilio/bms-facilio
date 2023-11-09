package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.bmsconsole.activity.CommandActivityType;
import com.facilio.bmsconsole.activity.ControlActionActivityType;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

public class PublishControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long controlActionId = (Long) context.get("controlActionId");
        V3ControlActionContext controlActionContext = ControlActionAPI.getControlAction(controlActionId);
        if(controlActionContext.getScheduledActionDateTime() <= System.currentTimeMillis()){
            throw new IllegalArgumentException("Schedule Action Time Must be Greater Than Current Time");
        }
        controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.PUBLISHED.getVal());
        ControlActionAPI.updateControlAction(controlActionContext);
        ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.PUBLISHED.getValue(), controlActionId);

        List<PeopleContext> firstLevelApproverList = ControlActionAPI.getApprovalList(controlActionContext.getId(),FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
        if(CollectionUtils.isNotEmpty(firstLevelApproverList)){
            controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getVal());
            ControlActionAPI.updateControlAction(controlActionContext);
            ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getValue(), controlActionId);
        }
        ControlActionAPI.generateCommand(controlActionId);
        return false;
    }
}
