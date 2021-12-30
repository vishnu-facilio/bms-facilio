package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.qa.QAndAUtil;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FetchQandARuleActionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long conditionId = (Long) context.get("conditionId");
        List<Map<String, Object>> evalRuleActionRel = QAndAUtil.fetchEvalRuleActionRel(conditionId);
        List<Long> actionsIds = evalRuleActionRel.stream().map(p -> (Long) p.get("actionId")).collect(Collectors.toList());
        List<ActionContext> actions = (ActionAPI.getActions(Collections.unmodifiableList(actionsIds)));
        context.put("actions",actions);
        return false;
    }
}
