package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFlaggedAlarmStatusDisplayNameCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedAlarms = (List<FlaggedEventContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(flaggedAlarms)) {
            for(FlaggedEventContext flaggedAlarm : flaggedAlarms) {
                if(flaggedAlarm != null) {
                    Map<String, Object> prop = new HashMap<>();
                    prop.put("statusDisplayName", flaggedAlarm.getStatus().getValue());
                    flaggedAlarm.addData(prop);
                }
            }
        }
        return false;
    }
}
