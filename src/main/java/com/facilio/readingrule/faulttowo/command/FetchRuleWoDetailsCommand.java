package com.facilio.readingrule.faulttowo.command;

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
        if(ruleId!=null){
            List<ReadingRuleWorkOrderRelContext> ruleWoDetails= RuleWoAPI.getRuleWoDetailsFromRuleId(ruleId);
            if(CollectionUtils.isNotEmpty(ruleWoDetails)) {
                for (ReadingRuleWorkOrderRelContext ruleWo : ruleWoDetails) {
                    if (ruleWo.getCriteriaId() != null) {
                        Map<Long, Criteria> criteriaProp = CriteriaAPI.getCriteriaAsMap(Collections.singletonList(ruleWo.getCriteriaId()));
                        ruleWo.setCriteria(criteriaProp.get(ruleWo.getCriteriaId()));
                    }
                }
            }
            context.put(FacilioConstants.ReadingRules.FAULT_TO_WORKORDER,ruleWoDetails);
        }
        return false;
    }
}
