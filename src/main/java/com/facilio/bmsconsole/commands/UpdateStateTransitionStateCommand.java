package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UpdateStateTransitionStateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long stateFlowId = (Long) context.get(FacilioConstants.ContextNames.STATE_FLOW_ID);
        StateflowTransitionContext transition = (StateflowTransitionContext) context.get(FacilioConstants.ContextNames.TRANSITION);
        if (stateFlowId != null && transition != null) {
            if (transition.getFromStateId() == -1 || transition.getToStateId() == -1) {
                throw new IllegalArgumentException("Invalid from and to state");
            }

            WorkflowRuleContext stateTransition = StateFlowRulesAPI.getStateTransition(stateFlowId, transition.getId());
            if (stateTransition == null) {
                throw new IllegalArgumentException("StateTransition cannot be found");
            }

            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getStateRuleTransitionFields());
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getStateRuleTransitionModule().getTableName())
                    .fields(Arrays.asList(fieldMap.get("fromStateId"), fieldMap.get("toStateId")))
                    .andCondition(CriteriaAPI.getIdCondition(transition.getId(), ModuleFactory.getStateRuleTransitionModule()));
            Map<String, Object> map = new HashMap<>();
            map.put("fromStateId", transition.getFromStateId());
            map.put("toStateId", transition.getToStateId());
            builder.update(map);
        }
        return false;
    }
}
