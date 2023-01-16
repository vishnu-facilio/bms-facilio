package com.facilio.bmsconsoleV3.signup.requestForQuotation;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestForQuotationStateTransition extends SignUpData {
    @Override
    public void addData() throws Exception {

        List<StateflowTransitionContext> stateTransitions = new ArrayList<>();
        StateflowTransitionContext quoteReceived = getStateflowTransitionContext("Quotes Received","isQuoteReceived");
        FacilioChain chain = TransactionChainFactory.updateWorkflowRuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,quoteReceived);
        chain.execute();
    }
    private StateflowTransitionContext getStateflowTransitionContext(String stateTransitionName, String fieldName) throws Exception {
        StateflowTransitionContext stateTransition = new StateflowTransitionContext();
        stateTransition.setId(getWorkFlowRuleId(stateTransitionName));
        List<ActionContext> actions = new ArrayList<>();
        ActionContext action = new ActionContext();
        List<Map> fieldMatcher = new ArrayList<>();
        Map<String,Object> field = new HashMap<>();
        field.put("field",fieldName);
        field.put("value","true");
        fieldMatcher.add(field);
        action.setActionType(13);
        stateTransition.setRuleType(28);
        JSONObject content = new JSONObject();
        content.put("fieldMatcher",fieldMatcher);
        action.setTemplateJson(content);
        actions.add(action);
        stateTransition.setActions(actions);
        return stateTransition;
    }
    private Long getWorkFlowRuleId(String stateTransition) throws Exception{
        FacilioModule module = ModuleFactory.getWorkflowRuleModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(module.getTableName())
                    .select(FieldFactory.getWorkflowRuleFields())
                    .andCondition(CriteriaAPI.getCondition("NAME", "name", stateTransition, StringOperators.IS));
            List<Map<String,Object>> rs = builder.get();
           if (rs != null && !rs.isEmpty()) {
              return (Long) rs.get(0).get("id");
           }
          return -1l;
    }
}
