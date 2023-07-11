package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.BureauInhibitReasonListContext;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.handlers.timer.TeamActionHandler;
import com.facilio.remotemonitoring.signup.BureauInhibitReasonListModule;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauActionModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
public class ComputeTimeRemainingForFlaggedEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for (FlaggedEventContext flaggedEvent : flaggedEvents) {
                if(flaggedEvent.getCurrentBureauActionDetail() != null) {
                    if(flaggedEvent.getCurrentBureauActionDetail() != null) {
                        FacilioContext actionContext = V3Util.getSummary(FlaggedEventBureauActionModule.MODULE_NAME, Collections.singletonList(flaggedEvent.getCurrentBureauActionDetail().getId()));
                        FlaggedEventBureauActionsContext actionsContext = (FlaggedEventBureauActionsContext) Constants.getRecordListFromContext(actionContext, FlaggedEventBureauActionModule.MODULE_NAME).get(0);
                        if(actionsContext.getEventStatus() != null) {
                            FlaggedEventBureauActionsContext.FlaggedEventBureauActionStatus eventStatus = actionsContext.getEventStatus();
                            if(eventStatus != null) {
                                TeamActionHandler handler = eventStatus.getTeamActionHandler();
                                if(handler != null) {
                                    flaggedEvent.setActionRemainingTime(handler.calculateRemainingTime(actionsContext,flaggedEvent));
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
