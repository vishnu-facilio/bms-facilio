package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.scoringrule.BaseScoringContext;
import com.facilio.bmsconsole.scoringrule.ScoringCommitmentContext;
import com.facilio.bmsconsole.scoringrule.ScoringRuleAPI;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.ScoreField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddOrUpdateScoringRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ScoringRuleContext scoringRuleContext = (ScoringRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        if (scoringRuleContext != null) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            scoringRuleContext.setRuleType(WorkflowRuleContext.RuleType.SCORING_RULE);
            scoringRuleContext.setActivityType(EventType.SCORING_RULE);
            scoringRuleContext.setModule(module);

//            if (scoringRuleContext.getScoreFieldId() < 0 && !scoringRuleContext.isDraft()) {
//                boolean alreadyCreated = false;
//                if (scoringRuleContext.getId() > 0) {
//                    ScoringRuleContext workflowRule = (ScoringRuleContext) WorkflowRuleAPI.getWorkflowRule(scoringRuleContext.getId());
//                    if (!workflowRule.isDraft()) {
//                        alreadyCreated = true;
//                    }
//                }
//                if (!alreadyCreated) {
//                    createScoreField(scoringRuleContext, module);
//                }
//            }

            List<Map<String, Object>> scoringContextMapList = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.SCORING_CONTEXT_LIST);
            scoringRuleContext.setScoringCommitmentContexts(FieldUtil.getAsBeanListFromMapList(scoringContextMapList, ScoringCommitmentContext.class));

            FacilioChain chain;
            if (scoringRuleContext.getId() < 0) {
                chain = TransactionChainFactory.addWorkflowRuleChain();
            }
            else {
                chain = TransactionChainFactory.updateWorkflowRuleChain();
            }
            FacilioContext ruleContext = chain.getContext();
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, scoringRuleContext);
            chain.execute();
        }
        return false;
    }
}
