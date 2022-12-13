package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.RCAGroupContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class FetchRCAGroupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NewReadingRuleContext> ruleList = NewReadingRuleAPI.destructureRuleFromRecordMap(context);
        for (NewReadingRuleContext rule : ruleList) {
            ReadingRuleRCAContext rca = rule.getRca();
            if (rca != null) {
                SelectRecordsBuilder<RCAGroupContext> builder = new SelectRecordsBuilder<RCAGroupContext>()
                        .moduleName(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE)
                        .select(Constants.getModBean().getAllFields(FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE))
                        .beanClass(RCAGroupContext.class)
                        .andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("rcaId", FacilioConstants.ReadingRules.RCA.RCA_GROUP_MODULE), Lists.newArrayList(rca.getId()), NumberOperators.EQUALS));

                List<RCAGroupContext> rcaGroupContexts = builder.get();
                if (CollectionUtils.isNotEmpty(rcaGroupContexts)) {
                    for (RCAGroupContext group : rcaGroupContexts) {
                        Criteria criteria = CriteriaAPI.getCriteria(group.getCriteriaId());
                        group.setCriteria(criteria);
                    }
                    rca.setGroups(rcaGroupContexts);
                }
            }
        }
        return false;
    }
}
