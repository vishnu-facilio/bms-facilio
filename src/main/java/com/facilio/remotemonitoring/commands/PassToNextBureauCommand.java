package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventBureauActionsContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class PassToNextBureauCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        FlaggedEventContext flaggedEvent = FlaggedEventUtil.getFlaggedEvent(id);
        if(flaggedEvent != null) {
            if(flaggedEvent.getCurrentBureauActionDetail() != null) {
                FlaggedEventBureauActionsContext nextBureauInAction = FlaggedEventUtil.getNextBureauEvaluation(flaggedEvent.getId(),flaggedEvent.getCurrentBureauActionDetail().getId());
                if(nextBureauInAction != null && nextBureauInAction.getId() > -1) {
                    FlaggedEventUtil.passToNextBureau(flaggedEvent,flaggedEvent.getCurrentBureauActionDetail());
                } else {
                    FacilioUtil.throwIllegalArgumentException(true,"No next bureau found for flagged event");
                }
            }
        }
        return false;
    }
}