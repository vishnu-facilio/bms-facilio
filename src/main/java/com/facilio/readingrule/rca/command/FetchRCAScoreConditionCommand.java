package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.RCAConditionScoreContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class FetchRCAScoreConditionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NewReadingRuleContext> ruleList = NewReadingRuleAPI.destructureRuleFromRecordMap(context);
        for (NewReadingRuleContext rule : ruleList) {
            ReadingRuleRCAContext rca = rule.getRca();
            if (rca != null) {
                List<RCAGroupContext> groups = rca.getGroups();
                if (CollectionUtils.isNotEmpty(groups)) {
                    for (RCAGroupContext group : groups) {
                        SelectRecordsBuilder<RCAConditionScoreContext> builder = new SelectRecordsBuilder<RCAConditionScoreContext>()
                                .moduleName(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE)
                                .select(Constants.getModBean().getAllFields(FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE))
                                .beanClass(RCAConditionScoreContext.class)
                                .andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("groupId", FacilioConstants.ReadingRules.RCA.RCA_SCORE_CONDITION_MODULE), Lists.newArrayList(group.getId()), NumberOperators.EQUALS));
                        List<RCAConditionScoreContext> rcaScoreConditions = builder.get();
                        if (CollectionUtils.isNotEmpty(rcaScoreConditions)) {
                            for (RCAConditionScoreContext scoreCondition : rcaScoreConditions) {
                                Criteria criteria = CriteriaAPI.getCriteria(scoreCondition.getCriteriaId());
                                scoreCondition.setCriteria(criteria);
                            }
                            group.setConditions(rcaScoreConditions);
                        }
                    }
                }
            }
        }
        return false;
    }
}
