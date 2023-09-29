package com.facilio.remotemonitoring.commands;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeleteFlaggedEventEmailNotificationRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        if (CollectionUtils.isEmpty(flaggedEventRules)) {
            List<Long> recordIds = (List<Long>) context.get("recordIds");
            if (CollectionUtils.isNotEmpty(recordIds)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(recordIds, ","), NumberOperators.EQUALS));
                flaggedEventRules = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventRuleModule.MODULE_NAME, null, FlaggedEventRuleContext.class, criteria, null);
            }
        }
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                if(flaggedEventRule.getEmailNotificationRuleId() != null) {
                    deleteRule(flaggedEventRule.getEmailNotificationRuleId());
                }
                if(flaggedEventRule.getDelayedEmailRuleOneId() != null) {
                    deleteRule(flaggedEventRule.getDelayedEmailRuleOneId());
                }
                if(flaggedEventRule.getDelayedEmailRuleTwoId() != null) {
                    deleteRule(flaggedEventRule.getDelayedEmailRuleTwoId());
                }
            }
        }
        return false;
    }

    private static void deleteRule(Long ruleId) throws Exception {
        if(ruleId != null && ruleId > -1) {
            FacilioContext ruleDeleteContext = new FacilioContext();
            ruleDeleteContext.put(FacilioConstants.ContextNames.ID, Collections.singletonList(ruleId));
            FacilioChain deleteRule = TransactionChainFactory.getDeleteWorkflowRuleChain();
            deleteRule.execute(ruleDeleteContext);
        }
    }
}
