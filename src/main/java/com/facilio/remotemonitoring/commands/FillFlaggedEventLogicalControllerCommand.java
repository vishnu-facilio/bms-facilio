package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillFlaggedEventLogicalControllerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(flaggedEvents)) {
            for(FlaggedEventContext event : flaggedEvents) {
                if(event != null && event.getController() == null) {
                    event.setController(RemoteMonitorUtils.getLogicalController());
                }
            }
        }
        return false;
    }
}
