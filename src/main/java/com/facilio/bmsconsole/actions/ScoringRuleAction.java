package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.util.List;
import java.util.Map;

public class ScoringRuleAction extends FacilioAction {

    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    private ScoringRuleContext scoringRule;
    public ScoringRuleContext getScoringRule() {
        return scoringRule;
    }
    public void setScoringRule(ScoringRuleContext scoringRule) {
        this.scoringRule = scoringRule;
    }

    private List<Map<String, Object>> scoringContexts;
    public List<Map<String, Object>> getScoringContexts() {
        return scoringContexts;
    }
    public void setScoringContexts(List<Map<String, Object>> scoringContexts) {
        this.scoringContexts = scoringContexts;
    }

    public String addOrUpdateScoringRule() throws Exception {
        FacilioChain chain = TransactionChainFactory.getAddOrUpdateScoringRuleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scoringRule);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.SCORING_CONTEXT_LIST, scoringContexts);
        chain.execute();

        setResult(FacilioConstants.ContextNames.WORKFLOW_RULE, context.get(FacilioConstants.ContextNames.WORKFLOW_RULE));

        return SUCCESS;
    }

    public String getScore() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getTempScoringChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, id);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setResult(FacilioConstants.ContextNames.RECORD, context.get(FacilioConstants.ContextNames.RECORD));

        return SUCCESS;
    }
}
