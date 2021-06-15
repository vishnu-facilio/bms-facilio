package com.facilio.qa.rules.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.rules.Constants;
import com.facilio.qa.rules.commands.QAndARuleReadOnlyChainFactory;
import com.facilio.qa.rules.commands.QAndARuleTransactionChainFactory;
import com.facilio.qa.rules.pojo.QAndARule;
import com.facilio.qa.rules.pojo.QAndARuleType;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QAndARuleAction extends V3Action {
    public String fetchOperators() throws Exception {
        FacilioChain fetchOperators = QAndARuleReadOnlyChainFactory.questionOperatorsChain();
        fetchOperators.execute();
        Object operators = fetchOperators.getContext().get(Constants.Command.QUESTION_TYPE_VS_OPERATORS);
        this.setData("operators", operators);

        return SUCCESS;
    }

    private long pageId;
    private QAndARuleType type;
    public String fetchRules() throws Exception {
        FacilioChain fetchRulesChain = QAndARuleReadOnlyChainFactory.fetchRulesChain();
        fetchRulesChain.getContext().put(Constants.Command.RULE_TYPE, type);
        fetchRulesChain.getContext().put(FacilioConstants.QAndA.Command.PAGE_ID, pageId);
        fetchRulesChain.execute();
        List<QAndARule> rules = (List<QAndARule>) fetchRulesChain.getContext().get(Constants.Command.RULES);
        this.setData("rules", rules);

        return SUCCESS;
    }

    private List<Map<String, Object>> rules;
    public String addRules() throws Exception {
        FacilioChain addOrUpdateRulesChain = QAndARuleTransactionChainFactory.addRules();
        FacilioContext context = addOrUpdateRulesChain.getContext();
        context.put(Constants.Command.RULE_TYPE, type);
        context.put(FacilioConstants.QAndA.Command.PAGE_ID, pageId);
        context.put(Constants.Command.RULES, rules);

        addOrUpdateRulesChain.execute();
        List<QAndARule> rules = (List<QAndARule>) context.get(Constants.Command.RULES);
        this.setData("rules", rules);

        return SUCCESS;
    }


    public String fetchScoringRules() throws Exception {
        this.type = QAndARuleType.SCORING;
        return fetchRules();
    }
    public String addScoringRules() throws Exception {
        this.type = QAndARuleType.SCORING;
        return addRules();
    }
}
