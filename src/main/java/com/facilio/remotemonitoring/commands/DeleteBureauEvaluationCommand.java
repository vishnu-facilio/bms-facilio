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
import com.facilio.modules.FieldUtil;
import com.facilio.remotemonitoring.context.FlaggedEventRuleBureauEvaluationContext;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.signup.FlaggedEventBureauEvaluationModule;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DeleteBureauEvaluationCommand extends FacilioCommand {
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
            for (FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition("FLAGGED_EVENT_RULE","flaggedEventRule",String.valueOf(flaggedEventRule.getId()),NumberOperators.EQUALS));
                List<FlaggedEventRuleBureauEvaluationContext> flaggedEventRuleBureauEvaluationContexts = V3RecordAPI.getRecordsListWithSupplements(FlaggedEventBureauEvaluationModule.MODULE_NAME,null, FlaggedEventRuleBureauEvaluationContext.class,criteria,null);
                if(CollectionUtils.isNotEmpty(flaggedEventRuleBureauEvaluationContexts)) {
                    Map<String, Object> deleteObj = new HashMap<>();
                    List<Long> ids = new ArrayList<>();
                    for(FlaggedEventRuleBureauEvaluationContext flaggedEventRuleBureauEvaluationContext : flaggedEventRuleBureauEvaluationContexts) {
                        if(flaggedEventRuleBureauEvaluationContext != null) {
                            ids.add(flaggedEventRuleBureauEvaluationContext.getId());
                            deleteRule(flaggedEventRuleBureauEvaluationContext.getEmailRuleId());
                        }
                    }
                    deleteObj.put(FlaggedEventBureauEvaluationModule.MODULE_NAME, ids);
                    V3Util.deleteRecords(FlaggedEventBureauEvaluationModule.MODULE_NAME, deleteObj,null,null,false);
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
