package com.facilio.bmsconsole.commands;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SetUserTriggerProps extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
        List<PMTriggerContext> triggers = pm.getTriggers();

        pm.setIsUserTriggerPresent(false);

        if (CollectionUtils.isEmpty(triggers)) {
            return false;
        }

        long count = triggers.stream().filter(i -> i.getTriggerExecutionSourceEnum() == PMTriggerContext.TriggerExectionSource.USER).count();
        if (count <= 0) {
            return false;
        }

        pm.setIsUserTriggerPresent(true);
        return false;
    }
}
