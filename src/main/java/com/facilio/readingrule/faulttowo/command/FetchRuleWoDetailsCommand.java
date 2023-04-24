package com.facilio.readingrule.faulttowo.command;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.readingrule.faulttowo.RuleWoAPI;
import com.facilio.readingrule.faulttowo.ReadingRuleWorkOrderRelContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FetchRuleWoDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long ruleId =(Long)context.get(FacilioConstants.ContextNames.RULE_ID);
        if(ruleId!=null) {
            List<Long> workFlowIds = RuleWoAPI.getRuleWoDetailsFromRuleId(ruleId);
            if (CollectionUtils.isNotEmpty(workFlowIds)) {
                List<WorkflowRuleContext> ruleWoDetails = WorkflowRuleAPI.getWorkflowRules(workFlowIds);
                if (CollectionUtils.isNotEmpty(ruleWoDetails)) {
                    for (WorkflowRuleContext ruleWo : ruleWoDetails) {
                        if (((ReadingRuleWorkOrderRelContext) ruleWo).getWoCriteriaId() != null) {
                            Map<Long, Criteria> criteriaProp = CriteriaAPI.getCriteriaAsMap(Collections.singletonList(((ReadingRuleWorkOrderRelContext) ruleWo).getWoCriteriaId()));
                            ((ReadingRuleWorkOrderRelContext) ruleWo).setWoCriteria(criteriaProp.get(((ReadingRuleWorkOrderRelContext) ruleWo).getWoCriteriaId()));
                        }
                    }
                }
                context.put(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER, ruleWoDetails);
            }
        }
        return false;
    }
}
