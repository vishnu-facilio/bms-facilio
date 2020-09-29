package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.scoringrule.BaseScoringContext;
import com.facilio.bmsconsole.scoringrule.NodeScoringContext;
import com.facilio.bmsconsole.scoringrule.ScoringRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExecuteScoringRulesCommand extends ExecuteAllWorkflowsCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteScoringRulesCommand.class.getSimpleName());

    public ExecuteScoringRulesCommand() {
        super(WorkflowRuleContext.RuleType.SCORING_RULE);
    }

    @Override
    protected List<WorkflowRuleContext> getWorkflowRules(FacilioModule module, List<EventType> activities, List<? extends ModuleBaseWithCustomFields> records, FacilioContext context) throws Exception {
        List<WorkflowRuleContext> workflowRules = super.getWorkflowRules(module, activities, records, context);
        if (CollectionUtils.isEmpty(workflowRules)) {
            return workflowRules;
        }

        List<WorkflowRuleContext> orderedList = new ArrayList<>();
        Set<Long> scoringFields = new HashSet<>();

        int threshold = 0;
        while (workflowRules.size() > 0) {
            if (threshold ++ > 20) {
                LOGGER.error("Reached max threshold of re-arranging scoring rules");
                return null;
            }
            Iterator<WorkflowRuleContext> iterator = workflowRules.iterator();
            while (iterator.hasNext()) {
                WorkflowRuleContext workflowRuleContext = iterator.next();
                boolean shouldAdd = true;

                ScoringRuleContext scoringRuleContext = (ScoringRuleContext) workflowRuleContext;
                List<BaseScoringContext> baseScoringContexts = scoringRuleContext.getBaseScoringContexts();
                for (BaseScoringContext baseScoringContext : baseScoringContexts) {
                    if (baseScoringContext instanceof NodeScoringContext) {
                        NodeScoringContext nodeScoringContext = (NodeScoringContext) baseScoringContext;
                        if (nodeScoringContext.getNodeTypeEnum() == NodeScoringContext.NodeType.CURRENT_MODULE) {
                            boolean contains = scoringFields.contains(nodeScoringContext.getScoringFieldId());
                            if (contains) {
                                // dependency already added
                                continue;
                            } else {
                                // dependency still not ordered
                                shouldAdd = false;
                                break;
                            }
                        }
                    }
                }

                if (shouldAdd) {
                    orderedList.add(workflowRuleContext);
                    scoringFields.add(scoringRuleContext.getScoreFieldId());
                    iterator.remove();
                }
            }
        }

        return orderedList;
    }
}
