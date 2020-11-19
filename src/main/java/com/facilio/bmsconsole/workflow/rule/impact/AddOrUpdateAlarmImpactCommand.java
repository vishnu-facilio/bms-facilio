package com.facilio.bmsconsole.workflow.rule.impact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.impact.util.AlarmImpactAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class AddOrUpdateAlarmImpactCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseAlarmImpactContext alarmImpact = (BaseAlarmImpactContext) context.get(FacilioConstants.ContextNames.ALARM_IMPACT);
        if (alarmImpact != null) {
            AlarmImpactAPI.addAlarmImpact(alarmImpact);
        }
        return false;
    }
}
