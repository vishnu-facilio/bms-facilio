package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import org.apache.commons.chain.Context;
public class AlarmNotReceivedForDurationJobScheduler extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        RemoteMonitorUtils.computeAndAddAlarmNotReceivedJob();
        return false;
    }
}