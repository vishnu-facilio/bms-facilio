package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.command.FacilioCommand;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.AlarmStrategy;
import com.facilio.remotemonitoring.context.AlarmTypeContext;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.mockito.internal.matchers.Null;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeleteExistingCriteriaForFilterRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<AlarmFilterRuleContext> alarmFilterRules = (List<AlarmFilterRuleContext>) recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(alarmFilterRules)) {
            for(AlarmFilterRuleContext alarmFilterRule : alarmFilterRules) {
                RemoteMonitorUtils.deleteExistingFilterRuleCriteriaForFilterRule(alarmFilterRule.getId());
            }
        }
        return false;
    }
}
