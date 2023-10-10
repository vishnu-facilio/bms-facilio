package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.FilteredAlarmContext;
import com.facilio.remotemonitoring.context.RawAlarmContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FillFilterAlarmLogicalControllerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FilteredAlarmContext> filteredAlarmContextList = (List<FilteredAlarmContext>) recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(filteredAlarmContextList)) {
            for (FilteredAlarmContext filterAlarm : filteredAlarmContextList) {
                if (filterAlarm != null && filterAlarm.getController() == null) {
                    filterAlarm.setController(RemoteMonitorUtils.getLogicalController());
                }
            }
        }
        return false;
    }
}
