package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlaggedEventClosureCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventContext> flaggedEvents = (List<FlaggedEventContext>) recordMap.get(FlaggedEventModule.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
        if (CollectionUtils.isNotEmpty(flaggedEvents)) {
            for (FlaggedEventContext flaggedEvent : flaggedEvents) {
                FlaggedEventRuleContext flaggedEventRule = alarmBean.getFlaggedEventRule(flaggedEvent.getFlaggedEventRule().getId());
                if(flaggedEventRule != null && flaggedEventRule.getFlaggedEventRuleClosureConfig() != null) {
                    FlaggedEventRuleClosureConfigContext closureConfig = FlaggedEventUtil.getFlaggedEventClosureConfig(flaggedEventRule.getFlaggedEventRuleClosureConfig().getId());
                    if(closureConfig != null) {
                        if(matchingStatus(closureConfig.getFlaggedEventStatuses(),flaggedEvent.getStatus())) {
                            flaggedEvent.setStatus(FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED);
                            V3RecordAPI.updateRecord(flaggedEvent,modBean.getModule(FlaggedEventModule.MODULE_NAME), Arrays.asList(modBean.getField("status",FlaggedEventModule.MODULE_NAME)));
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean matchingStatus(List<FlaggedEventContext.FlaggedEventStatus> ruleStatuses, FlaggedEventContext.FlaggedEventStatus eventStatus) {
        if(CollectionUtils.isNotEmpty(ruleStatuses) && eventStatus != null) {
            for(FlaggedEventContext.FlaggedEventStatus ruleStatus : ruleStatuses) {
                if(ruleStatus.equals(eventStatus)) {
                    return true;
                }
            }
        }
        return false;
    }
}
