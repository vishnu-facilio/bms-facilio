package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.bmsconsole.activity.ControlActionActivityType;
import com.facilio.bmsconsole.context.PeopleContext;
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
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class setInitialApprovalStatusForControlActionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap == null){
            return false;
        }
        List<V3ControlActionContext> controlActionContextList = (List<V3ControlActionContext>) recordMap.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(CollectionUtils.isEmpty(controlActionContextList)){
            return false;
        }
        for(V3ControlActionContext controlActionContext : controlActionContextList){
            if(controlActionContext.getControlActionSourceType() == V3ControlActionContext.ControlActionSourceTypeEnum.CONTROL_ACTION_TEMPLATE.getVal()){
                List<PeopleContext> firstLevelApproverList = ControlActionAPI.getApprovalList(controlActionContext.getId(),FacilioConstants.Control_Action.CONTROL_ACTION_FIRST_LEVEL_APPROVAL_MODULE_NAME);
                if(CollectionUtils.isNotEmpty(firstLevelApproverList)){
                    controlActionContext.setControlActionStatus(V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getVal());
                    ControlActionAPI.updateControlAction(controlActionContext);
                    ControlActionAPI.addControlActionActivity(V3ControlActionContext.ControlActionStatus.WAITING_FOR_FIRST_LEVEL_APPROVAL.getValue(),controlActionContext.getId());
                }
            }
        }
        return false;
    }
}
