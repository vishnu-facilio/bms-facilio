package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.beans.AlarmRuleBean;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleClosureConfigContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.Map;

public class CloseButtonDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        Map<String,Object> result = new HashMap<>();
        result.put("canClose", false);
        if(id != null) {
            FlaggedEventContext event = FlaggedEventUtil.getFlaggedEvent(id);
            if(event != null && event.getFlaggedEventRule() != null) {
                AlarmRuleBean alarmBean = (AlarmRuleBean) BeanFactory.lookup("AlarmBean");
                FlaggedEventRuleContext rule = alarmBean.getFlaggedEventRule(event.getFlaggedEventRule().getId());
                if (rule != null && rule.getFlaggedEventRuleClosureConfig() != null) {
                    FlaggedEventRuleClosureConfigContext closure = rule.getFlaggedEventRuleClosureConfig();
                    boolean canClose = FlaggedEventUtil.allowClose(event, closure);
                    result.put("canClose", canClose);
                    result.put("message", closure.getWarningMessage());
                }
            }
        }
        context.put(FacilioConstants.ContextNames.MESSAGE,result);
        return false;
    }
}
