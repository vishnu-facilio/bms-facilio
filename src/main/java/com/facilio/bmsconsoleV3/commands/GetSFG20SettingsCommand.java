package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SettingsContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Map;

public class GetSFG20SettingsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SFG20SettingsContext setting = SFG20JobPlanAPI.getSFG20Setting();
        context.put(FacilioConstants.ContextNames.SFG20.SETTING,setting);
        return false;
    }
}