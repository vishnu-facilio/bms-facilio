package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetFlaggedEventBureauActionRelatedRecordsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception{
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventBureauActionsContext> flaggedEventBureauActionList = (List<FlaggedEventBureauActionsContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(flaggedEventBureauActionList)){
            for(FlaggedEventBureauActionsContext flaggedEventBureauAction : flaggedEventBureauActionList){
                Long flaggedEventBureauActionId = flaggedEventBureauAction.getId();
                flaggedEventBureauAction.setConfiguredinhibitReasonList(RemoteMonitorUtils.getInhibitReasonList(flaggedEventBureauActionId));
                flaggedEventBureauAction.setCloseIssueReasonOptionList(RemoteMonitorUtils.getCloseIssueReasonOptionContexts(flaggedEventBureauActionId));
                Map<String, Object> prop = new HashMap<>();
                prop.put("statusDisplayName", flaggedEventBureauAction.getEventStatus().getValue());
                flaggedEventBureauAction.addData(prop);
            }
        }
        return false;
    }
}
