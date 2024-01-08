package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FlaggedAlarmPrecreateBeforeCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for (FlaggedEventContext flaggedEvent : flaggedEvents) {
                flaggedEvent.setSourceType(FlaggedEventContext.FlaggedEventSourceType.EVENT);
            }
        }
        return false;
    }
}
