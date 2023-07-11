package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FetchFilterRuleCriteriaForFilterRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                long alarmFilterRuleId = alarmFilterRule.getId();
                alarmFilterRule.setFilterRuleCriteriaList(RemoteMonitorUtils.fetchFilterRuleCriteriaForFilterRule(alarmFilterRuleId));
            }
        }
        return false;
    }
}
