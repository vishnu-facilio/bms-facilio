package com.facilio.readingrule.rca.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.rca.context.ReadingRuleRCAContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.v3.context.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;

public class FetchRuleRCACommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<NewReadingRuleContext> ruleList = NewReadingRuleAPI.destructureRuleFromRecordMap(context);
        for (NewReadingRuleContext rule : ruleList) {
            SelectRecordsBuilder<ReadingRuleRCAContext> builder = new SelectRecordsBuilder<ReadingRuleRCAContext>()
                    .moduleName(FacilioConstants.ReadingRules.RCA.RCA_MODULE)
                    .select(Constants.getModBean().getAllFields(FacilioConstants.ReadingRules.RCA.RCA_MODULE))
                    .beanClass(ReadingRuleRCAContext.class)
                    .andCondition(CriteriaAPI.getCondition(Constants.getModBean().getField("ruleId", FacilioConstants.ReadingRules.RCA.RCA_MODULE), Lists.newArrayList(rule.getId()), NumberOperators.EQUALS));

            List<ReadingRuleRCAContext> rcaList = builder.get();
            if (CollectionUtils.isNotEmpty(rcaList)) {
                rule.setRca(rcaList.get(0));
            }
        }
        return false;
    }
}
