package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCopyOfStateFlowCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(CreateCopyOfStateFlowCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        StateFlowRuleContext stateFlowContext = (StateFlowRuleContext) context.get(FacilioConstants.ContextNames.STATE_FLOW);
        Long originalStateFlowId = (Long) context.get(FacilioConstants.ContextNames.ID);
        if (originalStateFlowId != null && originalStateFlowId > 0 && stateFlowContext != null) {

            FacilioChain ruleChain = TransactionChainFactory.addWorkflowRuleChain();
            FacilioContext ruleContext = ruleChain.getContext();
            ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateFlowContext);
            ruleChain.execute();
            long createdStateFlowId = stateFlowContext.getId();
            context.put(FacilioConstants.ContextNames.STATE_FLOW, stateFlowContext);

            List<WorkflowRuleContext> allStateTransitionList = StateFlowRulesAPI.getAllStateTransitionList(originalStateFlowId);
            if (CollectionUtils.isNotEmpty(allStateTransitionList)) {
                Map<Long, Long> oldVsNewTransitionIds = new HashMap<>();
                for (WorkflowRuleContext workflowRuleContext : allStateTransitionList) {
                    long oldTransitionId = workflowRuleContext.getId();
                    StateflowTransitionContext stateflowTransitionContext = (StateflowTransitionContext) workflowRuleContext;
                    stateflowTransitionContext.setStateFlowId(createdStateFlowId);

                    ruleChain = TransactionChainFactory.addWorkflowRuleChain();
                    ruleContext = ruleChain.getContext();
                    ruleContext.put(FacilioConstants.ContextNames.WORKFLOW_RULE, stateflowTransitionContext);
                    ruleChain.execute();

                    oldVsNewTransitionIds.put(oldTransitionId, stateflowTransitionContext.getId());
                }

                updateStateflowDiagram(stateFlowContext, oldVsNewTransitionIds);
            }
            context.put(FacilioConstants.ContextNames.STATE_TRANSITION_LIST, allStateTransitionList);
        }
        return false;
    }

    private void updateStateflowDiagram(StateFlowRuleContext stateFlowContext, Map<Long, Long> oldVsNewTransitionIds) {
        try {
            String diagramJson = stateFlowContext.getDiagramJson();
            if (StringUtils.isNotEmpty(diagramJson)) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(diagramJson);
                if (jsonObject.containsKey("states")) {
                    JSONArray states = (JSONArray) jsonObject.get("states");
                    if (CollectionUtils.isNotEmpty(states)) {
                        for (int i = 0; i < states.size(); i++) {
                            JSONArray anchors = (JSONArray) ((JSONObject) states.get(i)).get("anchors");
                            if (CollectionUtils.isEmpty(states)) {
                                continue;
                            }

                            for (int j = 0; j < anchors.size(); j++) {
                                Object o = anchors.get(j);
                                if (o == null) {
                                    continue;
                                }
                                Object oldTransitionId = anchors.remove(j);
                                if (oldVsNewTransitionIds.containsKey(oldTransitionId)) {
                                    anchors.add(j, oldVsNewTransitionIds.get(oldTransitionId));
                                }
                            }
                        }
                    }
                }
                stateFlowContext.setDiagramJson(jsonObject.toJSONString());

                FacilioChain updateStateFlowDiagramChain = TransactionChainFactory.getUpdateStateFlowDiagramChain();
                FacilioContext updateStateFlowDiagramContext = updateStateFlowDiagramChain.getContext();
                updateStateFlowDiagramContext.put(FacilioConstants.ContextNames.RECORD_ID, stateFlowContext.getId());
                updateStateFlowDiagramContext.put(FacilioConstants.ContextNames.STATEFLOW_DIAGRAM, jsonObject);
                updateStateFlowDiagramChain.execute();
            }
        } catch (Exception ex) {
            LOGGER.error("Error in updating diagram in stateflow", ex);
        }
    }
}
