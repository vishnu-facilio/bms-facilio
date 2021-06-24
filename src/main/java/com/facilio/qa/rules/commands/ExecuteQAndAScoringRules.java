package com.facilio.qa.rules.commands;

import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.ResponseContext;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.qa.rules.pojo.ScoringRule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ExecuteQAndAScoringRules extends ExecuteQAndARulesCommand {
    public ExecuteQAndAScoringRules() {
        super(QAndARuleType.SCORING);
    }

    @Override
    protected <T extends QAndARule> List<T> fetchRules(QAndARuleType type, Long templateId, Collection<Long> questionIds, FacilioContext context) throws Exception {
        List<ScoringRule> rules = super.fetchRules(type, templateId, questionIds, context);
        if (CollectionUtils.isNotEmpty(rules)) {
            double fullScore = rules.stream().collect(Collectors.summingDouble(ScoringRule::fullScoreWithZeroOnNull));
            ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            response.setFullScore(fullScore);
        }
        return (List<T>) rules;
    }
}
