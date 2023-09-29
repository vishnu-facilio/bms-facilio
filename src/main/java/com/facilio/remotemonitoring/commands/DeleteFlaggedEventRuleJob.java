package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.context.AlarmFilterCriteriaType;
import com.facilio.remotemonitoring.context.AlarmFilterRuleContext;
import com.facilio.remotemonitoring.context.FilterRuleCriteriaContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleCriteriaModule;
import com.facilio.remotemonitoring.signup.AlarmFilterRuleModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteFlaggedEventRuleJob extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        List<Long> recordIds = null;
        if (CollectionUtils.isEmpty(flaggedEventRules)) {
            recordIds = (List<Long>) context.get("recordIds");
        } else {
            recordIds = flaggedEventRules.stream().map(FlaggedEventRuleContext::getId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(recordIds)) {
            for (Long id : recordIds) {
                FacilioTimer.deleteJob(id, RemoteMonitorConstants.FLAGGED_EVENT_SCHEDULED_JOB);
            }
        }
        return false;
    }
}