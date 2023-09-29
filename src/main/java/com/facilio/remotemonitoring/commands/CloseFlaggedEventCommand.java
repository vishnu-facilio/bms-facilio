package com.facilio.remotemonitoring.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.BureauCloseIssueReasonOptionContext;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CloseFlaggedEventCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        List<Long> closeValues = (List<Long>) context.get(RemoteMonitorConstants.CLOSE_VALUES);
        if(id != null) {
            if(id != null) {
                FlaggedEventContext event = FlaggedEventUtil.getFlaggedEvent(id);
                if(event != null && event.getFlaggedEventRule() != null) {
                    AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
                    FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(event.getFlaggedEventRule().getId());
                    if (rule != null && rule.getFlaggedEventRuleClosureConfig() != null) {
                        FlaggedEventRuleClosureConfigContext closure = rule.getFlaggedEventRuleClosureConfig();
                        boolean canClose = FlaggedEventUtil.allowClose(event, closure);
                        if(!canClose) {
                            FacilioUtil.throwIllegalArgumentException(true,"Cannot close the event as there are open alarms/Not authorised");
                        } else {
                            FlaggedEventUtil.closeFlaggedEvent(id,true,constructCloseIssuesReasonList(closeValues));
                        }
                    } else {
                        FlaggedEventUtil.closeFlaggedEvent(id,true,constructCloseIssuesReasonList(closeValues));
                    }
                }
            }
        }
        return false;
    }

    private List<BureauCloseIssueReasonOptionContext> constructCloseIssuesReasonList(List<Long> ids) {
        List<BureauCloseIssueReasonOptionContext> optionList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(ids)) {
            for(Long id : ids) {
                BureauCloseIssueReasonOptionContext reason = new BureauCloseIssueReasonOptionContext();
                reason.setId(id);
                optionList.add(reason);
            }
        }
        return optionList;
    }
}
